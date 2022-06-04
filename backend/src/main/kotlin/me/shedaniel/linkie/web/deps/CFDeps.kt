package me.shedaniel.linkie.web.deps

import kotlinx.coroutines.coroutineScope
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.curseforge.CurseForgeRequests
import me.shedaniel.linkie.web.curseforge.ModFile

class CFDeps(
    name: String,
    val modId: Int,
    val loaders: (ModFile, String) -> List<String>,
    val notation: (loader: String, mc: String, version: String) -> String,
    val versionGrabber: (ModFile) -> String,
    val mavens: List<MavenData>,
) : Deps(name) {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val modFiles = CurseForgeRequests.getAllModFiles(modId)
        modFiles.flatMap {
            it.sortableGameVersions.filter { it.gameVersion.isNotBlank() && it.gameVersion.tryToVersion() != null }.map { gameVersion ->
                gameVersion.gameVersionName to it
            }
        }.groupBy({ it.first }, { it.second }).forEach { (version, files) ->
            val file = files.filter { it.releaseType != 3 }.maxByOrNull { it.id } ?: return@forEach
            val modVersion = versionGrabber(file)
            loaders(file, version).forEach { loader ->
                val versionIdentifier = VersionIdentifier(
                    loader = loader,
                    version = version,
                    stable = true,
                )

                dependencies.getOrPut(versionIdentifier, ::mutableListOf).add(
                    Dependency(
                        name = name,
                        type = DependencyType.Api,
                        notation = notation(loader, version, modVersion),
                        version = modVersion
                    )
                )
            }
        }
        return@coroutineScope dependencies.mapValues {
            Data(mavens = mavens, dependencies = it.value)
        }
    }
}