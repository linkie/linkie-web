package me.shedaniel.linkie.web.deps

import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import me.shedaniel.linkie.utils.Version
import me.shedaniel.linkie.web.httpClient
import org.dom4j.io.SAXReader

object NeoForgeDeps : Deps("NeoForge") {
    override suspend fun provideData(): Map<VersionIdentifier, Data> {
        val dependencies = mutableMapOf<VersionIdentifier, MutableList<Dependency>>()
        val pom = httpClient.get("https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml").body<String>()
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

                a.zip(b).forEach { (x, y) ->
                    if (x != y) return@Comparator x.compareTo(y)
                }
                return@Comparator a.size.compareTo(b.size)
            }, ::getVersionParts))
            .distinctBy { getMinecraftVersion(it) }
            .forEach {
                val mcVersion = getMinecraftVersion(it) ?: return@forEach
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

    private fun getVersionParts(it: String): List<Int>? {
        if (it.contains('+') || it.contains("alpha")) return null
        return it.substringBefore('-').split('.').map { part -> part.toIntOrNull() ?: return null }
    }

    private fun getMinecraftVersion(it: String): String? {
        val parts = getVersionParts(it) ?: return null
        return when {
            parts.size == 3 && parts[0] >= 20 -> Version(1, parts[0], parts[1], null).toString()
            parts.size == 4 && parts[0] >= 26 -> Version(parts[0], parts[1], parts[2], null).toString()
            else -> null
        }
    }
}
