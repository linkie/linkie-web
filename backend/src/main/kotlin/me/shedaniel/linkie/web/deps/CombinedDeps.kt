package me.shedaniel.linkie.web.deps

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import me.shedaniel.linkie.utils.info

class CombinedDeps(
    name: String,
    vararg val deps: Deps,
) : Deps(name) {
    override suspend fun provideData(): Map<VersionIdentifier, Data> = coroutineScope {
        data class Inner(
            val mavens: MutableSet<MavenData> = mutableSetOf(),
            val dependencies: MutableList<Dependency> = mutableListOf(),
        )

        val datas = mutableMapOf<VersionIdentifier, Inner>()
        deps.map {
            launch {
                try {
                    info("Started dep cycle: ${it.name}")
                    it.provideData().also { data ->
                        data.forEach { (versionId, d) ->
                            val inner = datas.getOrPut(versionId, ::Inner)
                            inner.mavens.addAll(d.mavens)
                            inner.dependencies.addAll(d.dependencies)
                        }
                        info("Completed dep cycle: ${it.name}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.joinAll()
        return@coroutineScope datas.mapValues { (_, inner) ->
            Data(
                mavens = inner.mavens.toList(),
                dependencies = inner.dependencies
            )
        }
    }
}