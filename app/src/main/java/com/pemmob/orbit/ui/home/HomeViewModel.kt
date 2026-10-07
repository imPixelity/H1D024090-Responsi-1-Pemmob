package com.pemmob.orbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.orbit.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AnimeRepository = AnimeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadGenres()
        search(initial = true)
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, errorMessage = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            search()
        }
    }

    fun onGenreSelected(genreId: Int?) {
        _uiState.update { it.copy(selectedGenreId = genreId, errorMessage = null) }
        search()
    }

    fun retry() {
        search()
    }

    fun loadMore() {
        val snapshot = _uiState.value
        if (snapshot.isLoading || snapshot.isLoadingMore ||
            !snapshot.hasNextPage || snapshot.errorMessage != null
        ) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, loadMoreError = null) }
            repository.searchAnime(
                query = snapshot.query,
                genreId = snapshot.selectedGenreId,
                page = snapshot.currentPage + 1
            ).onSuccess { page ->
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        animeList = it.animeList + page.items,
                        currentPage = it.currentPage + 1,
                        hasNextPage = page.hasNextPage
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        loadMoreError = e.message
                            ?: "Gagal memuat halaman berikutnya."
                    )
                }
            }
        }
    }

    fun retryLoadMore() {
        _uiState.update { it.copy(loadMoreError = null) }
        loadMore()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repository.getGenres()
                .onSuccess { genres ->
                    _uiState.update { it.copy(genres = genres) }
                }
                .onFailure {
                    // Genre gagal tidak fatal, daftar tetap bisa dipakai
                }
        }
    }

    private fun search(initial: Boolean = false) {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    loadMoreError = null,
                    currentPage = 1,
                    hasNextPage = false
                )
            }
            repository.searchAnime(
                query = current.query,
                genreId = current.selectedGenreId,
                page = 1
            ).onSuccess { page ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        animeList = page.items,
                        errorMessage = null,
                        hasNextPage = page.hasNextPage
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat data. Periksa koneksi internet."
                    )
                }
            }
        }
    }
}
