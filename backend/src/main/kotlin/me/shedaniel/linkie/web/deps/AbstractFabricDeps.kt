package me.shedaniel.linkie.web.deps

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.*
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.curseforge.CurseForgeRequests
import me.shedaniel.linkie.web.curseforge.Pagination
import me.shedaniel.linkie.web.httpClient

abstract class AbstractFabricDeps(
    name: String,
    private val loader: String,
    private val metaUrl: String,
    val curseforgeId: Int,
    private val mappingsName: String,
    private val mappingsGroup: String,
    private val mavens: List<MavenData> = listOf()
) : Deps(name) {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val metaDeferred = async { httpClient.get(metaUrl).body<JsonObject>() }
        val pagination = async { CurseForgeRequests.getModFiles(curseforgeId, pageSize = 1).pagination }
        val meta = metaDeferred.await()
        val loaderVersion = meta["loader"]!!.jsonArray[0].jsonObject["version"]!!.jsonPrimitive.content
        val mappings = meta["mappings"]!!.jsonArray
        meta["game"]!!.jsonArray.asSequence().map(JsonElement::jsonObject).forEach { obj ->
            val version = obj["version"]!!.jsonPrimitive.content
            val release = version.tryToVersion().let { it != null && it.snapshot == null }
            val mappingsObj = mappings.firstOrNull { it.jsonObject["gameVersion"]!!.jsonPrimitive.content == version }?.jsonObject ?: return@forEach
            val yarnVersion = mappingsObj["version"]!!.jsonPrimitive.content
            val versionIdentifier = VersionIdentifier(
                loader = loader,
                version = version,
                stable = release,
            )
            dependencies.getOrPut(versionIdentifier, ::mutableListOf).apply {
                add(Dependency(mappingsName, DependencyType.Mappings, "$mappingsGroup:$yarnVersion:v2", yarnVersion))
                add(Dependency("Fabric Loader", DependencyType.Implementation, "net.fabricmc:fabric-loader:$loaderVersion", loaderVersion))
            }
        }
        fillFabricApi(pagination.await(), dependencies)
        return@coroutineScope dependencies.mapValues {
            Data(mavens, dependencies = it.value)
        }
    }

    abstract suspend fun fillFabricApi(pagination: Pagination, versions: MutableMap<VersionIdentifier, MutableList<Dependency>>)
}