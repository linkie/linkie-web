package me.shedaniel.linkie.web.curseforge

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.shedaniel.linkie.web.httpClient

object ModrinthRequests {
    private const val userAgent = "linkie/linkie-web (https://github.com/linkie/linkie-web)"
    suspend fun getProject(
        id: String,
    ): MRProject =
        httpClient.get("https://api.modrinth.com/v2/project/$id") {
            userAgent(userAgent)
        }.body()

    suspend fun getProjectFiles(
        id: String,
    ): List<MRProjectFile> =
        httpClient.get("https://api.modrinth.com/v2/project/$id/version") {
            userAgent(userAgent)
        }.body()
}