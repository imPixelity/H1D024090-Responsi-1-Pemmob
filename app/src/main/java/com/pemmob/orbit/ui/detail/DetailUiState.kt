package com.pemmob.orbit.ui.detail

import com.pemmob.orbit.data.remote.dto.AnimeDto

data class DetailUiState(
    val detail: AnimeDto? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
