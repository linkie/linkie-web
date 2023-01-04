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

object LegacyFabricDeps : Deps("Legacy Fabric") {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val metaDeferred = async { httpClient.get("https://meta.legacyfabric.net/v2/versions").body<JsonObject>() }
        val pagination = async { CurseForgeRequests.getModFiles(400281, pageSize = 1).pagination }
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
                add(Dependency("Legacy Yarn", DependencyType.Mappings, "net.legacyfabric:yarn:$yarnVersion:v2", yarnVersion))
                add(Dependency("Fabric Loader", DependencyType.Implementation, "net.fabricmc:fabric-loader:$loaderVersion", loaderVersion))
            }
        }
        fillFabricApi(pagination.await(), dependencies)
        return@coroutineScope dependencies.mapValues {
            Data(mavens = listOf(MavenData(
                subtitle = "Legacy Fabric",
                "https://repo.legacyfabric.net/repository/legacyfabric/"
            )), dependencies = it.value)
        }
    }

    private suspend fun fillFabricApi(pagination: Pagination, versions: MutableMap<VersionIdentifier, MutableList<Dependency>>) {
        val fileNameRegex = "Legacy Fabric API (.*)".toRegex()
        val addonFiles: MutableMap<String, MutableList<ModFile>> = mutableMapOf()
        val files = CurseForgeRequests.getAllModFiles(400281, pagination)
        files.forEach { file ->
            val displayedVersion = fileNameRegex.matchEntire(file.displayName)
            if (displayedVersion != null) {
                file.gameVersions.forEach { gameV ->
                    addonFiles.getOrPut(gameV, ::mutableListOf).add(file)
                }
            }
        }
        versions.forEach versionLoop@{ versionIdentifier, dependencies ->
            val match = addonFiles[versionIdentifier.version]?.toMutableList()?.sortedByDescending(ModFile::id)?.first()
            if (match != null) {
                val version = fileNameRegex.matchEntire(match.displayName)!!.groupValues[1]
                dependencies.add(Dependency("Legacy Fabric API", DependencyType.Implementation,
                    "net.legacyfabric.legacy-fabric-api:legacy-fabric-api:$version+${versionIdentifier.version}", version))
            }
        }
    }
}