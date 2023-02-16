package me.shedaniel.linkie.web.deps

import me.shedaniel.linkie.web.curseforge.CurseForgeRequests
import me.shedaniel.linkie.web.curseforge.ModFile
import me.shedaniel.linkie.web.curseforge.Pagination

object FabricDeps : AbstractFabricDeps(
    "Fabric",
    "https://meta.fabricmc.net/v2/versions",
    306612,
    "Yarn",
    "net.fabricmc:yarn"
) {
    override suspend fun fillFabricApi(pagination: Pagination, versions: MutableMap<VersionIdentifier, MutableList<Dependency>>) {
        val displayNameRegex = "\\[(.*)].*".toRegex()
        val fileNameRegex = "fabric(?:-api)?-(.*).jar".toRegex()
        val versionSplitRegex = "([^/]+)(./.*)".toRegex()
        val addonFiles: MutableMap<String, MutableList<ModFile>> = mutableMapOf()
        val files = CurseForgeRequests.getAllModFiles(this.curseforgeId, pagination)
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