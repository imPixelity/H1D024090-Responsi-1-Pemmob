package com.pemmob.orbit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeListResponse(
    @SerializedName("data") val data: List<AnimeDto> = emptyList(),
    @SerializedName("pagination") val pagination: PaginationDto? = null
)

data class AnimeDetailResponse(
    @SerializedName("data") val data: AnimeDto? = null
)

data class GenreListResponse(
    @SerializedName("data") val data: List<GenreDto> = emptyList()
)

data class PaginationDto(
    @SerializedName("last_visible_page") val lastVisiblePage: Int = 1,
    @SerializedName("has_next_page") val hasNextPage: Boolean = false,
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("items") val items: PaginationItemsDto? = null
)

data class PaginationItemsDto(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("per_page") val perPage: Int = 0
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("title_english") val titleEnglish: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("scored_by") val scoredBy: Int? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("episodes") val episodes: Int? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("genres") val genres: List<GenreDto> = emptyList(),
    @SerializedName("images") val images: AnimeImagesDto? = null
) {
    val displayTitle: String
        get() = titleEnglish?.takeIf { it.isNotBlank() } ?: title ?: "Unknown"
    val posterUrl: String?
        get() = images?.jpg?.largeImageUrl
            ?: images?.jpg?.imageUrl
}

data class AnimeImagesDto(
    @SerializedName("jpg") val jpg: JpgImageDto? = null
)

data class JpgImageDto(
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("small_image_url") val smallImageUrl: String? = null,
    @SerializedName("large_image_url") val largeImageUrl: String? = null
)

data class GenreDto(
    @SerializedName("mal_id") val malId: Int = 0,
    @SerializedName("name") val name: String = ""
)
