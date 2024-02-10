package me.shedaniel.linkie.web

import com.soywiz.korio.async.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.shedaniel.linkie.*
import me.shedaniel.linkie.namespaces.YarnNamespace
import me.shedaniel.linkie.utils.MemberEntry
import me.shedaniel.linkie.utils.ResultHolder
import me.shedaniel.linkie.utils.toVersion
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.deps.Deps
import me.shedaniel.linkie.web.deps.allDeps
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

val xml = XML {
    autoPolymorphic = true
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    prettyPrint = false
    isLenient = true
    explicitNulls = false
}

suspend fun search(
    namespaceStr: String, translateNsStr: String?, translateVersion: String?,
    version: String?, query: String, allowClasses: Boolean,
    allowMethods: Boolean, allowFields: Boolean, limit: Int,
): SearchResultEntries {
    require(limit in 1..1000) { "Limit must be between 1 and 1000" }
    if (translateNsStr != null) require(translateVersion == null) { "Cannot specify both translateNs and translateVersion" }
    if (translateVersion != null) require(translateNsStr == null) { "Cannot specify both translateNs and translateVersion" }
    val namespace =
        Namespaces.namespaces[namespaceStr] ?: throw IllegalArgumentException("No namespace found for $namespaceStr")
    val translateNamespace = translateNsStr?.let {
        Namespaces.namespaces[it] ?: throw IllegalArgumentException("No namespace found for $it")
    }

    val allVersions = namespace.getAllSortedVersions().toMutableList()
    if (translateNamespace != null) {
        allVersions.retainAll(translateNamespace.getAllSortedVersions().toSet())
    }

    val defaultVersion = namespace.defaultVersion.takeIf { it in allVersions }
        ?: (translateNamespace?.defaultVersion?.takeIf { it in allVersions } ?: allVersions.first())

    val provider = version?.let { namespace.getProvider(it) } ?: namespace.getProvider(defaultVersion)
    var mappings by Delegates.notNull<MappingsContainer>()
    var yarnMappings: MappingsContainer? = null
    var translateMappings: MappingsContainer? = null
    coroutineScope {
        launch { withContext(Dispatchers.Default) { mappings = provider.get() } }
        if (!namespace.hasMethodArgs(provider.version!!) && provider.version!!.tryToVersion()?.takeIf { it > "1.14.4".toVersion() } != null) {
            val yarnProvider = YarnNamespace.getProvider(provider.version!!)
            if (!yarnProvider.isEmpty()) {
                launch { withContext(Dispatchers.Default) { yarnMappings = yarnProvider.get() } }
            }
        }
        if (translateNamespace != null) {
            launch { withContext(Dispatchers.Default) { translateMappings = translateNamespace.getProvider(provider.version!!).get() } }
        } else if (translateVersion != null) {
            launch { withContext(Dispatchers.Default) { translateMappings = namespace.getProvider(translateVersion).get() } }
        }
        Unit
    }
    val result = MappingsQueryUtils.query(mappings, query, *buildList {
        if (allowClasses) add(MappingsEntryType.CLASS)
        if (allowMethods) add(MappingsEntryType.METHOD)
        if (allowFields) add(MappingsEntryType.FIELD)
    }.toTypedArray(), limit = limit)
    val argsMetadata: MethodArgMetadata = if (yarnMappings != null) {
        MethodArgMetadata(namespace.id.contains("mojang")) inner@{ clazz, method ->
            val obfName = method.obfMergedName ?: return@inner null
            val obfDesc = method.getObfMergedDesc(mappings)
            val parentObfName = clazz.obfMergedName ?: return@inner null
            val targetParent = yarnMappings!!.getClassByObfName(parentObfName) ?: return@inner null
            val targetMethod =
                targetParent.methods.firstOrNull { it.obfMergedName == obfName && it.getObfMergedDesc(yarnMappings!!) == obfDesc }
                    ?: return@inner null

            targetMethod.args
        }
    } else MethodArgMetadata(namespace)
    if (translateMappings == null) {
        return SearchResultEntries(
            entries = result.results.asSequence().take(limit).mapNotNull {
                toJsonFromEntry(mappings, it.value, it.score, argsMetadata)
            }.toList(),
            fuzzy = result.fuzzy,
        )
    } else {
        val elements = mutableListOf<JsonElement>()
        translate(mappings, translateMappings!!, translateNamespace != null, result.results.asSequence().take(limit)) { from, to, score ->
            toJsonFromEntry(mappings, from, score, argsMetadata)?.also { obj ->
                val translated = toJsonFromEntry(translateMappings!!, to, score, null)
                if (translated == null) elements.add(obj)
                else {
                    elements.add(buildJsonObject {
                        obj.forEach { key, value -> this.put(key, value) }
                        put("l", translated)
                    })
                }
            }
        }
        return SearchResultEntries(
            entries = elements,
            fuzzy = result.fuzzy,
        )
    }
}

suspend fun getSources(namespaceStr: String, version: String, className: String): String {
    val namespace =
        Namespaces.namespaces[namespaceStr] ?: throw IllegalArgumentException("No namespace found for $namespaceStr")
    val provider = namespace.getProvider(version).takeIf { !it.isEmpty() }
        ?: throw IllegalArgumentException("No provider found for $version")
    val file = File(provider.getSources(className).absolutePath)
    if (file.exists()) {
        return file.readText()
    } else {
        throw IllegalArgumentException("No source found for $className")
    }
}

fun getNamespaces() = Namespaces.namespaces.map { (id, namespace) ->
    NamespaceEntry(
        id,
        namespace.getAllSortedVersions().map { version ->
            VersionInfo(version, stable = version.tryToVersion()?.let { it.snapshot == null } == true)
        },
        namespace.supportsAT(), namespace.supportsAW(), namespace.supportsMixin(),
        namespace.supportsFieldDescription(), namespace.supportsSource(),
    )
}

fun getVersions(): MutableMap<String, MutableList<VersionInfo>> {
    val result = mutableMapOf<String, MutableList<VersionInfo>>()
    val finished = mutableSetOf<String>()
    for (deps in allDeps) {
        deps.datas.keys.forEach { versionIdentifier ->
            if (!finished.contains(versionIdentifier.loader)) {
                val versions = result.getOrPut(versionIdentifier.loader, ::mutableListOf)
                if (versions.none { it.version == versionIdentifier.version }) {
                    if (!versionIdentifier.version.endsWith("-Snapshot")) {
                        versions.add(
                            VersionInfo(
                                version = versionIdentifier.version,
                                stable = versionIdentifier.stable,
                            )
                        )
                    }
                }
            }
        }
        finished.addAll(result.keys)
    }
    return result
}

fun getAllLoaderVersions(): JsonObject {
    return buildJsonObject {
        val versions = getVersions()
        json.encodeToJsonElement(versions).jsonObject.forEach { loader, versionsArray ->
            val loaderVersions: MutableMap<String, MutableMap<String, DependencyData>> = getLoaderVersions(loader)
            this.putJsonArray(loader) {
                for (versionElement in versionsArray.jsonArray) {
                    addJsonObject {
                        versionElement.jsonObject.forEach { key, value -> this.put(key, value) }
                        val version = versionElement.jsonObject["version"]?.jsonPrimitive?.content

                        val blocks: MutableMap<String, DependencyData>? = loaderVersions[version]
                        blocks?.let { element ->
                            put("blocks", json.encodeToJsonElement(element))
                        }
                    }
                }
            }
        }
    }
}

fun getLoaderVersions(loader: String): MutableMap<String, MutableMap<String, DependencyData>> {
    val result = mutableMapOf<String, MutableMap<String, DependencyData>>()
    for (deps in allDeps) {
        deps.datas.forEach { (versionIdentifier, data) ->
            if (versionIdentifier.loader == loader) {
                if (versionIdentifier.version.endsWith("-Snapshot")) {
                    val stable = versionIdentifier.version.substringBefore("-Snapshot").toVersion()
                    val toList = result.keys.toList()
                    val stables =
                        toList.filter { it.tryToVersion()?.takeIf { version -> version.snapshot == null } != null }
                    val upperBound = stables.lastOrNull { it.toVersion() >= stable }?.let { toList.indexOf(it) } ?: -1
                    val lowerBound =
                        stables.firstOrNull { it.toVersion() < stable }?.let { toList.indexOf(it) } ?: toList.size
                    for (version in toList.subList(min(lowerBound, upperBound) + 1, max(lowerBound, upperBound))) {
                        result[version]?.set(
                            deps.name, DependencyData(
                                mavens = data.mavens,
                                dependencies = data.dependencies,
                                stable = versionIdentifier.stable,
                            )
                        )
                    }
                } else {
                    result.getOrPut(versionIdentifier.version, ::mutableMapOf)[deps.name] = DependencyData(
                        mavens = data.mavens,
                        dependencies = data.dependencies,
                        stable = versionIdentifier.stable,
                    )
                }
            }
        }
    }
    return result
}

fun toJsonFromEntry(mappings: MappingsContainer, entry: Any?, score: Double, argsMetadata: MethodArgMetadata?): JsonObject? {
    return when (entry) {
        is Class -> json.encodeToJsonElement(
            SearchResultClassEntry.serializer(), SearchResultClassEntry(
                obf = entry.obfMergedName,
                intermediary = entry.intermediaryName,
                named = entry.mappedName,
                obfClient = entry.obfClientName,
                obfServer = entry.obfServerName,
                score = score,
                memberType = "c",
            )
        ) as JsonObject

        is MemberEntry<*> -> (json.encodeToJsonElement(
            SearchResultMemberEntry.serializer(), SearchResultMemberEntry(
                obf = entry.member.obfMergedName,
                intermediary = entry.member.intermediaryName,
                named = entry.member.mappedName,
                ownerObf = entry.owner.obfMergedName,
                ownerIntermediary = entry.owner.intermediaryName,
                ownerNamed = entry.owner.mappedName,
                descObf = entry.member.getObfMergedDesc(mappings),
                descIntermediary = entry.member.intermediaryDesc,
                descNamed = entry.member.getMappedDesc(mappings),
                ownerObfClient = entry.owner.obfClientName,
                obfClient = entry.member.obfClientName,
                descObfClient = entry.member.getObfClientDesc(mappings),
                ownerObfServer = entry.owner.obfServerName,
                obfServer = entry.member.obfServerName,
                descObfServer = entry.member.getObfServerDesc(mappings),
                score = score,
                memberType = if (entry.member is Field) "f" else "m"
            )
        ) as JsonObject).let {
            if (entry.member is Method && argsMetadata != null) {
                var guessed = false
                val args = (entry.member as Method).args ?: (argsMetadata.guesser(entry.owner, entry.member as Method)?.also {
                    guessed = true
                } ?: return@let it)
                val map = it.toMutableMap()
                map["p"] = buildJsonObject {
                    args.forEach { arg ->
                        put(arg.index.toString(), arg.name)
                    }
                }
                map["q"] = JsonPrimitive(guessed)
                map["r"] = JsonPrimitive(!guessed && argsMetadata.isMojang)
                JsonObject(map)
            } else it
        }

        else -> null
    }
}

fun translate(
    source: MappingsContainer,
    target: MappingsContainer,
    matchObf: Boolean,
    results: Sequence<ResultHolder<*>>,
    callback: (Any, Any, Double) -> Unit,
) = if (matchObf) translateViaObf(source, target, results, callback)
else translateViaIntermediary(source, target, results, callback)

fun translateViaObf(
    source: MappingsContainer,
    target: MappingsContainer,
    results: Sequence<ResultHolder<*>>,
    callback: (Any, Any, Double) -> Unit,
) {
    results.forEach { (value, score) ->
        when {
            value is Class -> {
                val obfName = value.obfMergedName ?: return@forEach
                val targetClass = target.getClassByObfName(obfName) ?: return@forEach
                callback(value, targetClass, score)
            }

            value is MemberEntry<*> && value.member is Field -> {
                val parent = value.owner
                val field = value.member as Field

                val obfName = field.obfMergedName ?: return@forEach
                val parentObfName = parent.obfMergedName ?: return@forEach
                val targetParent = target.getClassByObfName(parentObfName) ?: return@forEach
                val targetField = targetParent.getFieldByObfName(obfName) ?: return@forEach

                callback(value, MemberEntry(targetParent, targetField), score)
            }

            value is MemberEntry<*> && value.member is Method -> {
                val parent = value.owner
                val method = value.member as Method

                val obfName = method.obfMergedName ?: return@forEach
                val obfDesc = method.getObfMergedDesc(source)
                val parentObfName = parent.obfMergedName ?: return@forEach
                val targetParent = target.getClassByObfName(parentObfName) ?: return@forEach
                val targetMethod =
                    targetParent.methods.firstOrNull { it.obfMergedName == obfName && it.getObfMergedDesc(target) == obfDesc }
                        ?: return@forEach

                callback(value, MemberEntry(targetParent, targetMethod), score)
            }
        }
    }
}

fun translateViaIntermediary(
    source: MappingsContainer,
    target: MappingsContainer,
    results: Sequence<ResultHolder<*>>,
    callback: (Any, Any, Double) -> Unit,
) {
    results.forEach { (value, score) ->
        when {
            value is Class -> {
                val intermediaryName = value.intermediaryName
                val targetClass = target.getClass(intermediaryName) ?: return@forEach
                callback(value, targetClass, score)
            }

            value is MemberEntry<*> && value.member is Field -> {
                val parent = value.owner
                val field = value.member as Field

                val intermediaryName = field.intermediaryName
                val parentIntermediaryName = parent.intermediaryName
                val targetParent = target.getClass(parentIntermediaryName) ?: return@forEach
                val targetField = targetParent.getField(intermediaryName) ?: return@forEach

                callback(value, MemberEntry(targetParent, targetField), score)
            }

            value is MemberEntry<*> && value.member is Method -> {
                val parent = value.owner
                val method = value.member as Method

                val intermediaryName = method.intermediaryName
                val intermediaryDesc = method.intermediaryDesc
                val parentIntermediaryName = parent.intermediaryName
                val targetParent = target.getClass(parentIntermediaryName) ?: return@forEach
                val targetMethod =
                    targetParent.methods.firstOrNull { it.intermediaryName == intermediaryName && it.intermediaryDesc == intermediaryDesc }
                        ?: return@forEach

                callback(value, MemberEntry(targetParent, targetMethod), score)
            }
        }
    }
}

@Serializable
data class VersionInfo(
    val version: String,
    val stable: Boolean,
)

@Serializable
data class DependencyData(
    val mavens: List<Deps.MavenData>,
    val dependencies: List<Deps.Dependency>,
    val stable: Boolean,
)

@Serializable
@XmlSerialName("oss", "", "")
data class OssEntries(
    val oss: List<OssEntry>,
)

@Serializable
data class SearchResultEntries(
    val entries: List<JsonElement>,
    val fuzzy: Boolean,
)

@Serializable
data class SearchResultClassEntry(
    @SerialName("o")
    val obf: String?,
    @SerialName("i")
    val intermediary: String,
    @SerialName("n")
    val named: String?,
    @SerialName("h")
    val obfClient: String?,
    @SerialName("l")
    val obfServer: String?,
    @SerialName("z")
    val score: Double,
    @SerialName("t")
    val memberType: String,
)

@Serializable
data class SearchResultMemberEntry(
    @SerialName("a")
    val ownerObf: String?,
    @SerialName("b")
    val ownerIntermediary: String,
    @SerialName("c")
    val ownerNamed: String?,
    @SerialName("o")
    val obf: String?,
    @SerialName("i")
    val intermediary: String,
    @SerialName("n")
    val named: String?,
    @SerialName("d")
    val descObf: String?,
    @SerialName("e")
    val descIntermediary: String,
    @SerialName("f")
    val descNamed: String?,
    @SerialName("g")
    val ownerObfClient: String?,
    @SerialName("h")
    val obfClient: String?,
    @SerialName("j")
    val descObfClient: String?,
    @SerialName("k")
    val ownerObfServer: String?,
    @SerialName("s")
    val obfServer: String?,
    @SerialName("m")
    val descObfServer: String?,
    @SerialName("z")
    val score: Double,
    @SerialName("t")
    val memberType: String,
)

@Serializable
@XmlSerialName("entry", "", "")
data class OssEntry(
    @XmlSerialName("name", "", "")
    @XmlElement(true)
    val name: String,
    @XmlSerialName("license", "", "")
    @XmlElement(true)
    val license: String,
    @XmlSerialName("link", "", "")
    @XmlElement(true)
    val link: String,
)

@Serializable
data class NamespaceEntry(
    val id: String,
    val versions: List<VersionInfo>,
    val supportsAT: Boolean,
    val supportsAW: Boolean,
    val supportsMixin: Boolean,
    val supportsFieldDescription: Boolean,
    val supportsSource: Boolean,
)

data class MethodArgMetadata(
    val isMojang: Boolean,
    val guesser: (Class, Method) -> List<MethodArg>?,
) {
    constructor(namespace: Namespace) : this(namespace.id.contains("mojang"), { _, _ -> null })
}
