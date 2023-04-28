package me.shedaniel.linkie.web.curseforge

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class MRProject(
    val approved: String?,
    val body: String,
    val categories: List<String>,
    val client_side: String,
    val color: Int?,
    val description: String,
    val discord_url: String?,
    val donation_urls: List<MRDonationUrl>,
    val downloads: Int,
    val followers: Int,
    val gallery: List<MRGallery>?,
    val game_versions: List<String>,
    val icon_url: String?,
    val id: String,
    val issues_url: String?,
    val license: MRLicense,
    val loaders: List<String>,
    val moderator_message: MRModeratorMessage?,
    val project_type: String,
    val published: String?,
    val server_side: String,
    val slug: String,
    val source_url: String?,
    val status: String,
    val team: String,
    val title: String,
    val updated: String?,
    val versions: List<String>,
    val wiki_url: String?,
)

@Serializable
data class MRDonationUrl(
    val id: String,
    val platform: String,
    val url: String,
)

@Serializable
data class MRGallery(
    val created: String,
    val description: String?,
    val featured: Boolean,
    val ordering: Int?,
    val title: String?,
    val url: String,
)

@Serializable
data class MRLicense(
    val id: String,
    val name: String,
    val url: String?,
)

@Serializable
data class MRModeratorMessage(
    val message: String,
    val body: String?,
)

@Serializable
data class MRProjectFile(
    val author_id: String,
    val changelog: String?,
    val date_published: String,
    val dependencies: List<MRDependency>,
    val downloads: Int,
    val featured: Boolean,
    val files: List<MRFile>,
    val game_versions: List<String>,
    val id: String,
    val loaders: List<String>,
    val name: String,
    val project_id: String,
    val requested_status: String?,
    val status: String,
    val version_number: String,
    val version_type: String
)

@Serializable
data class MRDependency(
    val dependency_type: String,
    val file_name: String?,
    val project_id: String?,
    val version_id: String?
)

@Serializable
data class MRFile(
    val file_type: String?,
    val filename: String,
    val hashes: MRHashes,
    val primary: Boolean,
    val size: Int,
    val url: String
)

@Serializable
class MRHashes(
    val sha512: String?,
    val sha1: String?
)