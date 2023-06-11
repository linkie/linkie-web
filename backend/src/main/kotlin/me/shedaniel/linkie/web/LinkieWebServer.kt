package me.shedaniel.linkie.web

import guru.zoroark.ratelimit.RateLimit
import guru.zoroark.ratelimit.rateLimited
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*
import me.shedaniel.linkie.Namespaces
import me.shedaniel.linkie.RemapperDaemon
import me.shedaniel.linkie.utils.tryToVersion
import me.shedaniel.linkie.web.deps.depsCycle
import me.shedaniel.linkie.web.deps.startDepsCycle
import me.shedaniel.linkie.web.deps.startLinkie

@Suppress("ExtractKtorModule")
fun main() {
    depsCycle()
    startDepsCycle()
    startLinkie()
    embeddedServer(Netty, port = 6969) {
        install(CORS) { anyHost() }
        install(IgnoreTrailingSlash)
        install(ContentNegotiation) {
            json(json)
        }
        install(RateLimit) {
            timeBeforeReset = java.time.Duration.ofMinutes(1)
            limit = 75
            limitMessage = """{"message":"You are being rate limited.","retry_after":{{retryAfter}}}"""
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
                cause.printStackTrace()
            }
        }
        routing {
            rateLimited {
                get("/api/versions") {
                    call.respond(getVersions())
                }
                get("/api/versions/{loader}") {
                    val loader = call.parameters["loader"]?.lowercase() ?: throw IllegalArgumentException("No loader specified")
                    if (loader == "all") {
                        call.respond(getAllLoaderVersions())
                    } else {
                        call.respond(getLoaderVersions(loader))
                    }
                }
                get("api/oss") {
                    call.respond(xml.decodeFromString(OssEntries.serializer(), this.javaClass.getResource("/licenses.xml")!!.readText())
                        .oss.map { it.copy(license = it.license.trim()) })
                }
                get("api/namespaces") {
                    call.respond(getNamespaces())
                }
                get("api/search") {
                    val namespaceStr = call.parameters["namespace"]?.lowercase() ?: throw IllegalArgumentException("No namespace specified")
                    val query = call.parameters["query"]
                        ?.replace('.', '/')?.replace('#', '/') ?: throw IllegalArgumentException("No query specified")
                    val version = call.parameters["version"]
                    val limit = call.parameters["limit"]?.toInt() ?: 100
                    val allowClasses = call.parameters["allowClasses"]?.toBoolean() ?: true
                    val allowMethods = call.parameters["allowMethods"]?.toBoolean() ?: true
                    val allowFields = call.parameters["allowFields"]?.toBoolean() ?: true
                    val translateNsStr = call.parameters["translate"]?.lowercase()
                    try {
                        call.respond(search(namespaceStr, translateNsStr, version, query, allowClasses, allowMethods, allowFields, limit))
                    } catch (error: NullPointerException) {
                        call.respond(buildJsonObject {
                            put("error", error.message ?: error.toString())
                        })
                        error.printStackTrace()
                    }
                }
                get("api/source") {
                    val namespaceStr = call.parameters["namespace"]?.lowercase() ?: throw IllegalArgumentException("No namespace specified")
                    val className = call.parameters["class"]?.replace('.', '/') ?: throw IllegalArgumentException("No class specified")
                    val version = call.parameters["version"] ?: throw IllegalArgumentException("No version specified")
                    call.respondText(getSources(namespaceStr, version, className))
                }
                get("api/status/sources/{namespace}") {
                    val namespaceStr = call.parameters["namespace"]?.lowercase() ?: throw IllegalArgumentException("No namespace specified")
                    if (namespaceStr == "next") {
                        val next = RemapperDaemon.nextInQueue
                        val nextJson = buildJsonObject {
                            put("namespace", next?.first?.id ?: "null")
                            if (next != null) {
                                put("version", next.second.version!!)
                            }
                        }
                        call.respond(buildJsonObject {
                            put("next", nextJson)
                            putJsonArray("list") {
                                RemapperDaemon.remapQueue.take(40).forEach { (ns, version, _) ->
                                    addJsonObject {
                                        put("namespace", ns.id)
                                        put("version", version)
                                    }
                                }
                            }
                        })
                        return@get
                    } else if (namespaceStr == "current") {
                        val current = RemapperDaemon.current
                        if (current == null) {
                            call.respond(buildJsonObject {
                                put("namespace", "null")
                            })
                        } else {
                            call.respond(buildJsonObject {
                                put("namespace", current.first.id)
                                put("version", current.second.version!!)
                            })
                        }
                        return@get
                    }
                    val namespace = Namespaces.namespaces[namespaceStr] ?: throw IllegalArgumentException("No namespace found for $namespaceStr")
                    call.respond(buildJsonObject {
                        namespace.getAllSortedVersions().forEach { version ->
                            val cache = RemapperDaemon.getCache(namespace, version)
                            put(version, buildJsonObject {
                                put("stable", version.tryToVersion()?.takeIf { it.snapshot == null } != null)
                                put("curr", cache?.classes ?: 0)
                                put("total", cache?.totalClasses ?: 0)
                                put("everCached", cache != null)
                            })
                        }
                    })
                }
            }
        }
    }.start(wait = true)
}
