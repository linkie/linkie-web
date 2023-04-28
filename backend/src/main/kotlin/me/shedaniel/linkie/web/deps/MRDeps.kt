package me.shedaniel.linkie.web.deps

import kotlinx.coroutines.coroutineScope
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.curseforge.MRProjectFile
import me.shedaniel.linkie.web.curseforge.ModrinthRequests

class MRDeps(
    name: String,
    val slug: String,
    val loaders: (MRProjectFile, String) -> List<String>,
    val notation: (loader: String, mc: String, version: String) -> String,
    val versionGrabber: (MRProjectFile) -> String,
    val mavens: List<MavenData>,
    val fileFilter: (String, MRProjectFile) -> Boolean = { _, _ -> true },
) : Deps(name) {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val modFiles = ModrinthRequests.getProjectFiles(slug)
        modFiles.flatMap { file ->
            file.game_versions.filter { version -> version.isNotBlank() && version.tryToVersion() != null }.map { gameVersion ->
                gameVersion to file
            }
        }.groupBy({ it.first }, { it.second }).forEach { (version, files) ->
            val file = files.filter { it.version_type == "release" || it.version_type == "beta" }.filter { fileFilter(version, it) }.maxByOrNull { it.id } ?: return@forEach
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