package me.shedaniel.linkie.web.curseforge

import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.request.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.shedaniel.linkie.web.Scopes
import me.shedaniel.linkie.web.httpClient
import kotlin.math.ceil

object CurseForgeRequests {
    var apiKey = "\$2a\$10\$j377O/SfRuuRxzaa8mH4dedRhIoOO9XLAz/oX8RAtINqXhHgBpqGW"

    suspend fun searchMods(
        gameId: Int,
        classId: Int? = null,
        categoryId: Int? = null,
        gameVersion: String? = null,
        searchFilter: String? = null,
        sortField: ModsSearchSortField? = null,
        sortOrder: ModsSearchSortOrder? = null,
        modLoaderType: String? = null,
        gameVersionTypeId: Int? = null,
        slug: String? = null,
        index: Int? = null,
        pageSize: Int? = null,
    ): SearchMods =
        httpClient.get("https://api.curseforge.com/v1/mods/search") {
            headers { append("x-api-key", apiKey) }
            parameter("gameId", gameId)
            parameter("classId", classId)
            parameter("categoryId", categoryId)
            parameter("gameVersion", gameVersion)
            parameter("searchFilter", searchFilter)
            parameter("sortField", sortField?.id)
            parameter("sortOrder", sortOrder?.id)
            parameter("modLoaderType", modLoaderType)
            parameter("gameVersionTypeId", gameVersionTypeId)
            parameter("slug", slug)
            parameter("index", index)
            parameter("pageSize", pageSize)
        }.body()

    suspend fun getMod(
        modId: Int,
    ): Mod =
        httpClient.get("https://api.curseforge.com/v1/mods/$modId") {
            headers { append("x-api-key", apiKey) }
        }.body<GetMod>().data

    suspend fun getMods(
        modIds: Iterable<Int>,
    ): List<Mod> = coroutineScope {
        return@coroutineScope modIds.map { modId ->
            async { getMod(modId) }
        }.awaitAll()
//        client.get<GetMods>("https://api.curseforge.com/v1/mods") {
//            headers { append("x-api-key", apiKey) }
//            contentType(ContentType.Application.Json)
//            body = GetModsRequest(modIds.toList())
//        }.data
    }

    suspend fun getModFile(
        modId: Int,
        fileId: Int,
    ): ModFile =
        httpClient.get("https://api.curseforge.com/v1/mods/$modId/files/$fileId") {
            headers { append("x-api-key", apiKey) }
        }.body<GetModFile>().data

    suspend fun getModFiles(
        modId: Int,
        gameVersionTypeId: Int? = null,
        index: Int? = null,
        pageSize: Int = 50,
    ): GetModFiles =
        httpClient.get("https://api.curseforge.com/v1/mods/$modId/files") {
            headers { append("x-api-key", apiKey) }
            parameter("gameVersionTypeId", gameVersionTypeId)
            parameter("index", index)
            parameter("pageSize", pageSize)
        }.body()

    suspend fun getFiles(
        fileIds: Iterable<Int>,
    ): List<ModFile> =
        httpClient.get("https://api.curseforge.com/v1/mods/files") {
            headers { append("x-api-key", apiKey) }
            setBody("""{"fileIds":[${fileIds.joinToString(",")}]}""")
        }.body<GetFiles>().data

    suspend fun getAllModFiles(
        modId: Int,
    ): List<ModFile> {
        val pagination = getModFiles(modId, pageSize = 1).pagination
        return getAllModFiles(modId, pagination)
    }

    suspend fun getAllModFiles(
        modId: Int,
        pagination: Pagination,
    ): List<ModFile> = coroutineScope {
        val jobs = mutableListOf<Deferred<List<ModFile>>>()
        val pages = ceil(pagination.totalCount!! / 50.0).toInt()
        for (i in 0 until pages) {
            jobs.add(async {
                getModFiles(
                    modId,
                    index = 50 * i,
                    pageSize = 50,
                ).data
            })
        }
        return@coroutineScope jobs.awaitAll().flatten()
    }
}