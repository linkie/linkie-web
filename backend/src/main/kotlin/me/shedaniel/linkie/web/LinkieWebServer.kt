package me.shedaniel.linkie.web

import guru.zoroark.ratelimit.RateLimit
import guru.zoroark.ratelimit.rateLimited
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import me.shedaniel.linkie.Class
import me.shedaniel.linkie.Field
import me.shedaniel.linkie.MappingsContainer
import me.shedaniel.linkie.MappingsEntryType
import me.shedaniel.linkie.Method
import me.shedaniel.linkie.Namespaces
import me.shedaniel.linkie.getClassByObfName
import me.shedaniel.linkie.getMappedDesc
import me.shedaniel.linkie.getObfClientDesc
import me.shedaniel.linkie.getObfMergedDesc
import me.shedaniel.linkie.getObfServerDesc
import me.shedaniel.linkie.obfClientName
import me.shedaniel.linkie.obfMergedName
import me.shedaniel.linkie.obfServerName
import me.shedaniel.linkie.utils.MemberEntry
import me.shedaniel.linkie.utils.ResultHolder
import me.shedaniel.linkie.utils.toVersion
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.deps.Deps
import me.shedaniel.linkie.web.deps.allDeps
import me.shedaniel.linkie.web.deps.depsCycle
import me.shedaniel.linkie.web.deps.startDepsCycle
import me.shedaniel.linkie.web.deps.startLinkie
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.io.File
import kotlin.math.max
import kotlin.math.min

val xml = XML {
    autoPolymorphic = true
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    prettyPrint = false
    isLenient = true
    explicitNulls = false
}

@Suppress("ExtractKtorModule")
fun main() {
    depsCycle()
    startDepsCycle()
    startLinkie()
    embeddedServer(Netty, port = 6969) {
        install(CORS) { anyHost() }
        install(IgnoreTrailingSlash)
        install(ContentNegotiation) {
            json(json)
        }
        install(RateLimit) {
            timeBeforeReset = java.time.Duration.ofMinutes(1)
            limit = 75
            limitMessage = """{"message":"You are being rate limited.","retry_after":{{retryAfter}}}"""
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
                cause.printStackTrace()
            }
        }
        routing {
            rateLimited {
                get("/api/versions") {
                    call.respond(getVersions())
                }
                get("/api/versions/{loader}") {
                    val loader = call.parameters["loader"]?.lowercase() ?: throw IllegalArgumentException("No loader specified")
                    if (loader == "all") {
                        call.respond(buildJsonObject {
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
                        })
                    } else {
                        call.respond(getLoaderVersions(loader))
                    }
                }
                get("api/oss") {
                    call.respond(xml.decodeFromString(OssEntries.serializer(), this.javaClass.getResource("/licenses.xml")!!.readText())
                        .oss.map { it.copy(license = it.license.trim()) })
                }
                get("api/namespaces") {
                    call.respond(Namespaces.namespaces.map { (id, namespace) ->
                        NamespaceEntry(
                            id,
                            namespace.getAllSortedVersions().map { version ->
                                VersionInfo(version, stable = version.tryToVersion()?.let { it.snapshot == null } == true)
                            },
                            namespace.supportsAT(), namespace.supportsAW(), namespace.supportsMixin(),
                            namespace.supportsFieldDescription(), namespace.supportsSource() && false,
                        )
                    })
                }
                get("api/search") {
                    val namespaceStr = call.parameters["namespace"]?.lowercase() ?: throw IllegalArgumentException("No namespace specified")
                    val query = call.parameters["query"]
                        ?.replace('.', '/')?.replace('#', '/') ?: throw IllegalArgumentException("No query specified")
                    val version = call.parameters["version"]
                    val limit = call.parameters["limit"]?.toInt() ?: 100
                    val allowClasses = call.parameters["allowClasses"]?.toBoolean() ?: true
                    val allowMethods = call.parameters["allowMethods"]?.toBoolean() ?: true
                    val allowFields = call.parameters["allowFields"]?.toBoolean() ?: true
                    val translateNsStr = call.parameters["translate"]?.lowercase()
                    require(limit in 1..1000) { "Limit must be between 1 and 1000" }
                    val namespace = Namespaces.namespaces[namespaceStr] ?: throw IllegalArgumentException("No namespace found for $namespaceStr")
                    val translateNamespace = translateNsStr?.let { Namespaces.namespaces[it] ?: throw IllegalArgumentException("No namespace found for $it") }

                    val allVersions = namespace.getAllSortedVersions().toMutableList()
                    if (translateNamespace != null) {
                        allVersions.retainAll(translateNamespace.getAllSortedVersions())
                    }

                    val defaultVersion = namespace.defaultVersion.takeIf { it in allVersions } ?: (translateNamespace?.defaultVersion?.takeIf { it in allVersions } ?: allVersions.first())

                    val provider = version?.let { namespace.getProvider(it) } ?: namespace.getProvider(defaultVersion)
                    val mappings = provider.get()
                    try {
                        val result = MappingsQueryUtils.query(mappings, query, *buildList {
                            if (allowClasses) add(MappingsEntryType.CLASS)
                            if (allowMethods) add(MappingsEntryType.METHOD)
                            if (allowFields) add(MappingsEntryType.FIELD)
                        }.toTypedArray(), limit = limit)
                        if (translateNamespace == null) {
                            call.respond(
                                SearchResultEntries(
                                    entries = result.results.asSequence().take(limit).mapNotNull {
                                        toJsonFromEntry(mappings, it.value, it.score)
                                    }.toList(),
                                    fuzzy = result.fuzzy,
                                )
                            )
                        } else {
                            val target = translateNamespace.getProvider(provider.version!!).get()
                            val elements = mutableListOf<JsonElement>()
                            translate(mappings, target, result.results.asSequence().take(limit)) { from, to, score ->
                                toJsonFromEntry(mappings, from, score)?.also { obj ->
                                    val translated = toJsonFromEntry(target, to, score)
                                    if (translated == null) elements.add(obj)
                                    else {
                                        elements.add(buildJsonObject {
                                            obj.forEach { key, value -> this.put(key, value) }
                                            put("l", translated)
                                        })
                                    }
                                }
                            }
                            call.respond(
                                SearchResultEntries(
                                    entries = elements,
                                    fuzzy = result.fuzzy,
                                )
                            )
                        }
                    } catch (error: NullPointerException) {
                        call.respond(buildJsonObject {
                            put("error", error.message ?: error.toString())
                        })
                        error.printStackTrace()
                    }
                }
                get("api/source") {
                    val namespaceStr = call.parameters["namespace"]?.lowercase() ?: throw IllegalArgumentException("No namespace specified")
                    val className = call.parameters["class"]?.replace('.', '/') ?: throw IllegalArgumentException("No class specified")
                    val version = call.parameters["version"] ?: throw IllegalArgumentException("No version specified")
                    val namespace = Namespaces.namespaces[namespaceStr] ?: throw IllegalArgumentException("No namespace found for $namespaceStr")
                    val provider = namespace.getProvider(version).takeIf { !it.isEmpty() } ?: throw IllegalArgumentException("No provider found for $version")
                    val file = File(provider.getSources(className).absolutePath)
                    if (file.exists()) {
                        call.respondText(file.readText())
                    } else {
                        throw IllegalArgumentException("No source found for $className")
                    }
                }
            }
        }
    }.start(wait = true)
}

private fun getVersions(): MutableMap<String, MutableList<VersionInfo>> {
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

private fun getLoaderVersions(loader: String): MutableMap<String, MutableMap<String, DependencyData>> {
    val result = mutableMapOf<String, MutableMap<String, DependencyData>>()
    for (deps in allDeps) {
        deps.datas.forEach { (versionIdentifier, data) ->
            if (versionIdentifier.loader == loader) {
                if (versionIdentifier.version.endsWith("-Snapshot")) {
                    val stable = versionIdentifier.version.substringBefore("-Snapshot").toVersion()
                    val toList = result.keys.toList()
                    val stables = toList.filter { it.tryToVersion()?.takeIf { version -> version.snapshot == null } != null }
                    val upperBound = stables.lastOrNull { it.toVersion() >= stable }?.let { toList.indexOf(it) } ?: -1
                    val lowerBound = stables.firstOrNull { it.toVersion() < stable }?.let { toList.indexOf(it) } ?: toList.size
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

fun toJsonFromEntry(mappings: MappingsContainer, entry: Any?, score: Double): JsonObject? {
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

        is MemberEntry<*> -> json.encodeToJsonElement(
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
        ) as JsonObject

        else -> null
    }
}

private fun translate(
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
                val targetField = targetParent.fields.firstOrNull { it.obfMergedName == obfName } ?: return@forEach

                callback(value, MemberEntry(targetParent, targetField), score)
            }

            value is MemberEntry<*> && value.member is Method -> {
                val parent = value.owner
                val method = value.member as Method

                val obfName = method.obfMergedName ?: return@forEach
                val obfDesc = method.getObfMergedDesc(source)
                val parentObfName = parent.obfMergedName ?: return@forEach
                val targetParent = target.getClassByObfName(parentObfName) ?: return@forEach
                val targetMethod = targetParent.methods.firstOrNull { it.obfMergedName == obfName && it.getObfMergedDesc(target) == obfDesc } ?: return@forEach

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
    @SerialName("l")
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
