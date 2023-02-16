package me.shedaniel.linkie.web.deps

import me.shedaniel.linkie.web.curseforge.CurseForgeRequests
import me.shedaniel.linkie.web.curseforge.ModFile
import me.shedaniel.linkie.web.curseforge.Pagination

object LegacyFabricDeps : AbstractFabricDeps(
    "Legacy Fabric",
    "legacy-fabric",
    "https://meta.legacyfabric.net/v2/versions",
    400281,
    "Legacy Yarn",
    "net.legacyfabric:yarn",
    listOf(MavenData(
        subtitle = "Legacy Fabric",
        "https://repo.legacyfabric.net/repository/legacyfabric/"
    ))
) {
    override suspend fun fillFabricApi(pagination: Pagination, versions: MutableMap<VersionIdentifier, MutableList<Dependency>>) {
        val fileNameRegex = "Legacy Fabric API (.*)".toRegex()
        val addonFiles: MutableMap<String, MutableList<ModFile>> = mutableMapOf()
        val files = CurseForgeRequests.getAllModFiles(this.curseforgeId, pagination)
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