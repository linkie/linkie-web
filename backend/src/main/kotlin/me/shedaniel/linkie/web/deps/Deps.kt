package me.shedaniel.linkie.web.deps

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.shedaniel.linkie.LinkieConfig
import me.shedaniel.linkie.Namespaces
import me.shedaniel.linkie.namespaces.LegacyYarnNamespace
import me.shedaniel.linkie.namespaces.MCPNamespace
import me.shedaniel.linkie.namespaces.MojangHashedNamespace
import me.shedaniel.linkie.namespaces.MojangNamespace
import me.shedaniel.linkie.namespaces.MojangSrgNamespace
import me.shedaniel.linkie.namespaces.PlasmaNamespace
import me.shedaniel.linkie.namespaces.QuiltMappingsNamespace
import me.shedaniel.linkie.namespaces.YarnNamespace
import me.shedaniel.linkie.namespaces.YarrnNamespace
import me.shedaniel.linkie.utils.info
import me.shedaniel.linkie.utils.toVersion
import me.shedaniel.linkie.utils.tryToVersion
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

typealias ModLoader = String

data class VersionIdentifier(
    val loader: ModLoader,
    val version: String,
    val stable: Boolean,
)

val allDeps = listOf(
    FabricDeps,
    ForgeDeps,
    CFDeps(
        "Architectury API", 419699,
        loaders = { _, _ -> listOf("fabric", "forge") },
        notation = { loader, version -> "dev.architectury:architectury-${if (loader == "quilt") "fabric" else loader}:$version" },
        versionGrabber = { file -> file.displayName.substringAfterLast("v") },
        mavens = listOf(
            Deps.MavenData(
                url = "https://maven.architectury.dev/",
            ),
            Deps.MavenData(
                subtitle = "Alternative Maven Repository",
                url = "https://maven.shedaniel.me/",
            ),
        )
    ),
    CFDeps(
        "Roughly Enough Items", 310111,
        loaders = { _, version ->
            when {
                version.tryToVersion() == null || version.toVersion().snapshot != null -> listOf("fabric")
                version.toVersion() >= "1.16.5".toVersion() -> listOf("fabric", "forge")
                version.toVersion() > "1.13.2".toVersion() -> listOf("fabric")
                else -> listOf()
            }
        },
        notation = { loader, version -> "me.shedaniel:RoughlyEnoughItems-$loader:$version" },
        versionGrabber = { file -> file.displayName.substringAfterLast("v").replace(" Build ", "+build.") },
        mavens = listOf(
            Deps.MavenData(
                url = "https://maven.shedaniel.me/",
            ),
            Deps.MavenData(
                subtitle = "Alternative Maven Repository",
                url = "https://maven.architectury.dev/",
            ),
        )
    ),
)

fun startDepsCycle() {
    val service = Executors.newScheduledThreadPool(1)
    service.scheduleAtFixedRate(::depsCycle, 1, 1, TimeUnit.MINUTES)
}

fun startLinkie() {
    val service = Executors.newFixedThreadPool(1)
    service.execute {
        Namespaces.init(
            LinkieConfig.DEFAULT.copy(
                namespaces = listOf(
                    YarnNamespace,
                    MojangNamespace,
                    MojangSrgNamespace,
                    MojangHashedNamespace,
                    QuiltMappingsNamespace,
                    MCPNamespace,
                    LegacyYarnNamespace,
                    YarrnNamespace,
                    PlasmaNamespace,
                )
            )
        )
    }
}

fun depsCycle() {
    runBlocking {
        GlobalScope.launch {
            info("Started deps cycle")
            allDeps.map {
                launch {
                    try {
                        info("Started dep cycle: ${it.name}")
                        it.provideData().also { data ->
                            it.datas.clear()
                            it.datas.putAll(data)
                            info("Completed dep cycle: ${it.name}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.joinAll()
            info("Completed deps cycle")
        }.join()
    }
}

abstract class Deps(val name: String) {
    protected val json = Json { ignoreUnknownKeys = true }
    val datas = mutableMapOf<VersionIdentifier, Data>()

    abstract suspend fun provideData(): Map<VersionIdentifier, Data>

    @Serializable
    data class Data(
        val mavens: List<MavenData>,
        val dependencies: List<Dependency>,
    )

    @Serializable
    data class MavenData(
        val subtitle: String? = null,
        val url: String,
    )

    @Serializable
    data class Dependency(
        val name: String,
        val type: DependencyType,
        val notation: String,
        val version: String,
    )

    @Serializable
    enum class DependencyType {
        Api,
        Implementation,
        CompileOnly,
        RuntimeOnly,
        Mappings,
        Forge,
    }
}
