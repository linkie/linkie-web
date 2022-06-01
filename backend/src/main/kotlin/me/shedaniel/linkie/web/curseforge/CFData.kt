package me.shedaniel.linkie.web.curseforge

import kotlinx.serialization.Serializable

@Serializable
data class SearchMods(
    val data: List<Mod>,
    val pagination: Pagination,
)

@Serializable
data class GetMod(
    val data: Mod,
)

@Serializable
data class GetMods(
    val data: List<Mod>,
)

@Serializable
data class GetModsRequest(
    val modIds: List<Int>,
)

@Serializable
data class GetModFile(
    val data: ModFile,
)

@Serializable
data class GetModFiles(
    val data: List<ModFile>,
    val pagination: Pagination,
)

@Serializable
data class GetFiles(
    val data: List<ModFile>,
)

@Serializable
data class Pagination(
    // A zero based index of the first item that is included in the response
    val index: Int,
    // The requested number of items to be included in the response
    val pageSize: Int,
    // The actual number of items that were included in the response
    val resultCount: Int,
    // The total number of items available by the request
    val totalCount: Int?,
)

@Serializable
data class Mod(
    val allowModDistribution: Boolean?,
    val authors: List<Author>,
    val categories: List<Category>,
    val classId: Int?,
    val dateCreated: String,
    val dateModified: String,
    val dateReleased: String,
    val downloadCount: Double,
    val gameId: Int,
    val gamePopularityRank: Int,
    val id: Int,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val latestFiles: List<ModFile>,
    val latestFilesIndexes: List<FileIndex>,
    val links: Links,
    val logo: Logo?,
    val mainFileId: Int,
    val name: String,
    val primaryCategoryId: Int,
    val screenshots: List<Screenshot>,
    val slug: String,
    val status: Int,
    val summary: String,
)

@Serializable
data class Author(
    val id: Int,
    val name: String,
    val url: String,
)

@Serializable
data class Category(
    val classId: Int?,
    val dateModified: String,
    val gameId: Int,
    val iconUrl: String,
    val id: Int,
    val isClass: Boolean?,
    val name: String,
    val parentCategoryId: Int?,
    val slug: String?,
    val url: String,
)

@Serializable
data class FileIndex(
    val fileId: Int,
    val filename: String,
    val gameVersion: String,
    val gameVersionTypeId: Int?,
    val modLoader: Int?,
    val releaseType: Int,
)

@Serializable
data class Links(
    val issuesUrl: String?,
    val sourceUrl: String?,
    val websiteUrl: String?,
    val wikiUrl: String?,
)

@Serializable
data class Logo(
    val description: String,
    val id: Int,
    val modId: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String,
)

@Serializable
data class Screenshot(
    val description: String,
    val id: Int,
    val modId: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String,
)

@Serializable
data class Dependency(
    val modId: Int,
    val relationType: Int,
)

@Serializable
data class Hashe(
    val algo: Int,
    val value: String,
)

@Serializable
data class FileModule(
    val fingerprint: Long,
    val name: String,
)

@Serializable
data class SortableGameVersion(
    val gameVersion: String,
    val gameVersionName: String,
    val gameVersionPadded: String,
    val gameVersionReleaseDate: String,
    val gameVersionTypeId: Int?,
)

@Serializable
data class ModFile(
    val alternateFileId: Int?,
    val dependencies: List<Dependency>,
    val displayName: String,
    val downloadCount: Long,
    val downloadUrl: String?,
    val exposeAsAlternative: Boolean?,
    val fileDate: String,
    val fileFingerprint: Long,
    val fileLength: Int,
    val fileName: String,
    val fileStatus: Int,
    val gameId: Int,
    val gameVersions: List<String>,
    val hashes: List<Hashe>,
    val id: Int,
    val isAvailable: Boolean,
    val isServerPack: Boolean?,
    val modId: Int,
    val modules: List<FileModule>,
    val parentProjectFileId: Int?,
    val releaseType: Int,
    val serverPackFileId: Int?,
    val sortableGameVersions: List<SortableGameVersion>,
)

enum class ModsSearchSortField(val id: Int) {
    Featured(1),
    Popularity(2),
    LastUpdated(3),
    Name(4),
    Author(5),
    TotalDownloads(6),
    Category(7),
    GameVersion(8),
}

enum class ModsSearchSortOrder(val id: String) {
    Ascending("asc"),
    Descending("desc"),
}
