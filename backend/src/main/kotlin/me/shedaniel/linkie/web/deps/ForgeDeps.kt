package me.shedaniel.linkie.web.deps

import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.httpClient
import org.dom4j.io.SAXReader

object ForgeDeps : Deps("Forge") {
    override suspend fun provideData(): Map<VersionIdentifier, Data> {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val pom = httpClient.get("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml").body<String>()
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
            .distinctBy { it.substringBefore('-') }
            .forEach {
                val mcVersion = it.substringBefore('-')
                val mcVersionSemVer = mcVersion.tryToVersion() ?: return@forEach
                val forgeVersion = it.substring(mcVersion.length + 1).substringBeforeLast('-')
                val versionIdentifier = VersionIdentifier(
                    loader = "forge",
                    version = mcVersion,
                    stable = true,
                )
                dependencies.getOrPut(versionIdentifier, ::mutableListOf).add(
                    Dependency(
                        name = "Forge",
                        type = DependencyType.Forge,
                        notation = "net.minecraftforge:forge:$mcVersion-$forgeVersion",
                        version = forgeVersion
                    )
                )
            }
        return dependencies.mapValues {
            Data(mavens = listOf(), dependencies = it.value)
        }
    }
}