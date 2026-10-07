package com.pemmob.orbit.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pemmob.orbit.ui.components.ErrorView
import com.pemmob.orbit.ui.components.LoadingView
import com.pemmob.orbit.ui.components.OrbitBackground
import com.pemmob.orbit.ui.components.orbitTopBarBrush

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Anime") },
                modifier = Modifier.background(orbitTopBarBrush()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        OrbitBackground {
            when {
                state.isLoading -> LoadingView(
                    modifier = Modifier.padding(innerPadding)
                )
                state.errorMessage != null -> ErrorView(
                    message = state.errorMessage!!,
                    onRetry = viewModel::retry,
                    modifier = Modifier.padding(innerPadding)
                )
            state.detail != null -> {
                val anime = state.detail!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = anime.posterUrl,
                        contentDescription = anime.displayTitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = anime.displayTitle,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Score",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = anime.score?.toString() ?: "-",
                            style = MaterialTheme.typography.titleMedium
                        )
                        anime.scoredBy?.let {
                            Text(
                                text = " ($it votes)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    DetailRow(label = "Tipe", value = anime.type ?: "-")
                    DetailRow(label = "Rating", value = anime.rating ?: "-")
                    DetailRow(
                        label = "Episode",
                        value = anime.episodes?.toString() ?: "Unknown"
                    )
                    DetailRow(label = "Status", value = anime.status ?: "-")
                    Text(
                        text = "Genre",
                        style = MaterialTheme.typography.titleSmall
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (anime.genres.isEmpty()) {
                            Text("-", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            anime.genres.map { it.name }.forEach { name ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(name) }
                                )
                            }
                        }
                    }
                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = anime.synopsis?.takeIf { it.isNotBlank() }
                            ?: "Sinopsis tidak tersedia.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
