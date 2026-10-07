package com.pemmob.orbit.ui.home

import com.pemmob.orbit.data.remote.dto.AnimeDto
import com.pemmob.orbit.data.remote.dto.GenreDto

data class HomeUiState(
    val query: String = "",
    val animeList: List<AnimeDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val selectedGenreId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreError: String? = null
)
