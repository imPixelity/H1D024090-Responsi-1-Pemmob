package com.pemmob.orbit.data.repository

import com.pemmob.orbit.data.remote.RetrofitClient
import com.pemmob.orbit.data.remote.TenraiApiService
import com.pemmob.orbit.data.remote.dto.AnimeDto
import com.pemmob.orbit.data.remote.dto.GenreDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepository(
    private val api: TenraiApiService = RetrofitClient.api
) {
    suspend fun searchAnime(
        query: String,
        genreId: Int? = null,
        page: Int = 1
    ): Result<AnimePage> = withContext(Dispatchers.IO) {
        try {
            val q = query.trim().takeIf { it.isNotBlank() } ?: ""
            val response = api.searchAnime(
                query = q,
                genres = genreId?.toString(),
                page = page,
                limit = 24,
                sfw = true
            )
            Result.success(
                AnimePage(
                    items = response.data,
                    hasNextPage = response.pagination?.hasNextPage ?: false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeDetail(malId: Int): Result<AnimeDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeDetail(malId)
                val detail = response.data
                if (detail != null) Result.success(detail)
                else Result.failure(NoSuchElementException("Detail anime tidak ditemukan"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getGenres(): Result<List<GenreDto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeGenres()
                val filtered = response.data
                    .filterNot { it.malId in EXPLICIT_GENRE_IDS }
                    .sortedBy { it.name }
                Result.success(filtered)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    companion object {
        private val EXPLICIT_GENRE_IDS = setOf(9, 12, 26, 28, 35, 49, 65, 81)
    }
}

data class AnimePage(
    val items: List<AnimeDto>,
    val hasNextPage: Boolean
)
