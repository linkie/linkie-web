package me.shedaniel.linkie.web.deps

import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import me.shedaniel.linkie.utils.Version
import me.shedaniel.linkie.utils.toVersion
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.httpClient
import org.dom4j.io.SAXReader

object NeoForgeDeps : Deps("NeoForge") {
    override suspend fun provideData(): Map<VersionIdentifier, Data> {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val pom = httpClient.get("https://maven.neoforged.net/net/neoforged/neoforge/maven-metadata.xml").body<String>()
        SAXReader().read(pom.byteInputStream()).rootElement
            .element("versioning")
            .element("versions")
            .elementIterator("version")
            .asSequence()
            .map { it.text }
            .sortedWith(compareByDescending(Comparator { a, b ->
                if (a === b) return@Comparator 0
                if (a == null) return@Comparator -1
                if (b == null) return@Comparator 1

                var c = a.major.compareTo(b.major)
                if (c != 0) return@Comparator c
                c = a.minor.compareTo(b.minor)
                if (c != 0) return@Comparator c
                return@Comparator a.patch.compareTo(b.patch)
            }, String::tryToVersion))
            .distinctBy { getMinecraftVersion(it) }
            .forEach {
                val mcVersion = getMinecraftVersion(it)
                val mcVersionSemVer = mcVersion.tryToVersion() ?: return@forEach
                val neoforgeVersion = it
                val versionIdentifier = VersionIdentifier(
                    loader = "neoforge",
                    version = mcVersion,
                    stable = true,
                )
                dependencies.getOrPut(versionIdentifier, ::mutableListOf).add(
                    Dependency(
                        name = "NeoForge",
                        type = DependencyType.NeoForge,
                        notation = "net.neoforged:neoforge:$neoforgeVersion",
                        version = neoforgeVersion
                    )
                )
            }
        return dependencies.mapValues {
            Data(mavens = listOf(), dependencies = it.value)
        }
    }

    private fun getMinecraftVersion(it: String): String {
        val (major, minor, patch, snapshot) = it.toVersion()
        return Version(1, major, minor, null).toString();
    }
}