package com.pemmob.orbit.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pemmob.orbit.ui.components.AnimeCard
import com.pemmob.orbit.ui.components.EmptyView
import com.pemmob.orbit.ui.components.ErrorView
import com.pemmob.orbit.ui.components.GenreDropdown
import com.pemmob.orbit.ui.components.LoadingView
import com.pemmob.orbit.ui.components.OrbitBackground
import com.pemmob.orbit.ui.components.orbitTopBarBrush
import com.pemmob.orbit.ui.theme.PacificoFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAnimeClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var isFilterVisible by rememberSaveable { mutableStateOf(false) }
    var focusPending by rememberSaveable { mutableStateOf(false) }
    var expandPending by rememberSaveable { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Panel dibuka user
    LaunchedEffect(focusPending) {
        if (focusPending) {
            searchFocusRequester.requestFocus()
            keyboardController?.show()
            focusPending = false
        }
    }
    // Panel ditutup
    LaunchedEffect(isSearchVisible) {
        if (!isSearchVisible) keyboardController?.hide()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Orbit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = PacificoFont,
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                modifier = Modifier.background(orbitTopBarBrush()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = {
                        if (!isSearchVisible) focusPending = true
                        isSearchVisible = !isSearchVisible
                    }) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = if (isSearchVisible) "Tutup pencarian" else "Buka pencarian"
                        )
                    }
                    IconButton(onClick = {
                        if (!isFilterVisible) expandPending = true
                        isFilterVisible = !isFilterVisible
                    }) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = if (isFilterVisible) "Tutup filter genre" else "Buka filter genre"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        OrbitBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedVisibility(visible = isSearchVisible) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .focusRequester(searchFocusRequester),
                        placeholder = { Text("Cari judul anime…") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cari") },
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        singleLine = true
                    )
                }
                AnimatedVisibility(visible = isFilterVisible) {
                    GenreDropdown(
                        genres = state.genres,
                        selectedGenreId = state.selectedGenreId,
                        onGenreSelected = viewModel::onGenreSelected,
                        autoExpand = expandPending,
                        onAutoExpandConsumed = { expandPending = false }
                    )
                }
                when {
                    state.isLoading -> LoadingView(modifier = Modifier.weight(1f))
                    state.errorMessage != null -> ErrorView(
                        message = state.errorMessage!!,
                        onRetry = viewModel::retry,
                        modifier = Modifier.weight(1f)
                    )

                    state.animeList.isEmpty() -> EmptyView(modifier = Modifier.weight(1f))
                    else -> {
                        val gridState = rememberLazyGridState()
                        // Mentok bawah (≤4 item dari ujung) → muat halaman berikutnya otomatis
                        val reachedBottom by remember {
                            derivedStateOf {
                                val lastVisible =
                                    gridState.layoutInfo.visibleItemsInfo.lastOrNull()
                                lastVisible != null &&
                                        lastVisible.index >= state.animeList.lastIndex - 4
                            }
                        }
                        LaunchedEffect(
                            reachedBottom,
                            state.hasNextPage,
                            state.isLoadingMore,
                            state.isLoading
                        ) {
                            if (reachedBottom) viewModel.loadMore()
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.animeList, key = { it.malId }) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onAnimeClick(anime.malId) }
                                )
                            }
                            if (state.isLoadingMore) {
                                item(span = { GridItemSpan(2) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            if (state.loadMoreError != null) {
                                item(span = { GridItemSpan(2) }) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = state.loadMoreError!!,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        TextButton(onClick = viewModel::retryLoadMore) {
                                            Text("Coba lagi")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
