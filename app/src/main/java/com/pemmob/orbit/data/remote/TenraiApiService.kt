package com.pemmob.orbit.data.remote

import com.pemmob.orbit.data.remote.dto.AnimeDetailResponse
import com.pemmob.orbit.data.remote.dto.AnimeListResponse
import com.pemmob.orbit.data.remote.dto.GenreListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TenraiApiService {
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 24,
        @Query("sfw") sfw: Boolean = true,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null
    ): AnimeListResponse

    @GET("anime/{id}/full")
    suspend fun getAnimeDetail(
        @Path("id") malId: Int
    ): AnimeDetailResponse

    @GET("genres/anime")
    suspend fun getAnimeGenres(): GenreListResponse

    companion object {
        const val BASE_URL = "https://api.tenrai.org/v1/"
    }
}
