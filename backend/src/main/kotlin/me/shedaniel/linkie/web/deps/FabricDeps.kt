package me.shedaniel.linkie.web.deps

import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.shedaniel.linkie.utils.info
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.Scopes
import me.shedaniel.linkie.web.curseforge.CurseForgeRequests
import me.shedaniel.linkie.web.curseforge.ModFile
import me.shedaniel.linkie.web.curseforge.Pagination
import me.shedaniel.linkie.web.httpClient
import kotlin.math.ceil

object FabricDeps : Deps("Fabric") {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val metaDeferred = async { httpClient.get("https://meta.fabricmc.net/v2/versions").body<JsonObject>() }
        val pagination = async { CurseForgeRequests.getModFiles(306612, pageSize = 1).pagination }
        val meta = metaDeferred.await()
        val loaderVersion = meta["loader"]!!.jsonArray[0].jsonObject["version"]!!.jsonPrimitive.content
        val mappings = meta["mappings"]!!.jsonArray
        meta["game"]!!.jsonArray.asSequence().map(JsonElement::jsonObject).forEach { obj ->
            val version = obj["version"]!!.jsonPrimitive.content
            val release = version.tryToVersion().let { it != null && it.snapshot == null }
            val mappingsObj = mappings.firstOrNull { it.jsonObject["gameVersion"]!!.jsonPrimitive.content == version }?.jsonObject ?: return@forEach
            val yarnVersion = mappingsObj["version"]!!.jsonPrimitive.content
            val versionIdentifier = VersionIdentifier(
                loader = "fabric",
                version = version,
                stable = release,
            )
            dependencies.getOrPut(versionIdentifier, ::mutableListOf).apply {
                add(Dependency("Yarn", DependencyType.Mappings, "net.fabricmc:yarn:$yarnVersion:v2", yarnVersion))
                add(Dependency("Fabric Loader", DependencyType.Implementation, "net.fabricmc:fabric-loader:$loaderVersion", loaderVersion))
            }
        }
        fillFabricApi(pagination.await(), dependencies)
        return@coroutineScope dependencies.mapValues {
            Data(mavens = listOf(), dependencies = it.value)
        }
    }

    private suspend fun fillFabricApi(pagination: Pagination, versions: MutableMap<VersionIdentifier, MutableList<Dependency>>) {
        val displayNameRegex = "\\[(.*)].*".toRegex()
        val fileNameRegex = "fabric(?:-api)?-(.*).jar".toRegex()
        val versionSplitRegex = "([^/]+)(./.*)".toRegex()
        val addonFiles: MutableMap<String, MutableList<ModFile>> = mutableMapOf()
        val files = CurseForgeRequests.getAllModFiles(306612, pagination)
        files.forEach { file ->
            val displayedVersion = displayNameRegex.matchEntire(file.displayName)?.groupValues?.get(1) ?: return@forEach
            val splitMatchResult = versionSplitRegex.matchEntire(displayedVersion)
            if (splitMatchResult == null) {
                addonFiles.getOrPut(displayedVersion, ::mutableListOf).add(file)
            } else {
                val allVersions = splitMatchResult.groupValues[2].split('/').map {
                    if (it.length == 1) splitMatchResult.groupValues[1] + it else it
                }
                allVersions.forEach { version ->
                    addonFiles.getOrPut(version, ::mutableListOf).add(file)
                }
            }
        }
        versions.forEach versionLoop@{ versionIdentifier, dependencies ->
            val match = addonFiles[versionIdentifier.version]?.toMutableList()?.sortedByDescending(ModFile::id)?.first()
            if (match != null) {
                val version = fileNameRegex.matchEntire(match.fileName)!!.groupValues[1]
                dependencies.add(Dependency("Fabric API", DependencyType.Implementation, "net.fabricmc.fabric-api:fabric-api:$version", version))
            }
        }
    }
}