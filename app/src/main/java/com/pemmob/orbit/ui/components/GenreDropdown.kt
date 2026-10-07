package com.pemmob.orbit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pemmob.orbit.data.remote.dto.GenreDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdown(
    genres: List<GenreDto>,
    selectedGenreId: Int?,
    onGenreSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    // True → menu langsung terbuka sekali, lalu dilaporkan lewat
    // onAutoExpandConsumed agar tidak terbuka lagi saat kembali dari navigasi
    autoExpand: Boolean = false,
    onAutoExpandConsumed: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(autoExpand) {
        if (autoExpand) {
            expanded = true
            onAutoExpandConsumed()
        }
    }
    val selectedName = genres.find { it.malId == selectedGenreId }?.name
        ?: "Semua Genre"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Genre") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Semua Genre") },
                onClick = {
                    onGenreSelected(null)
                    expanded = false
                }
            )
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre.name) },
                    onClick = {
                        onGenreSelected(
                            if (selectedGenreId == genre.malId) null else genre.malId
                        )
                        expanded = false
                    },
                    trailingIcon = {
                        if (selectedGenreId == genre.malId) {
                            Text("✓")
                        }
                    }
                )
            }
        }
    }
}
