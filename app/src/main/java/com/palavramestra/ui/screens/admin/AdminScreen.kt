package com.palavramestra.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palavramestra.PalavraMestraApp
import com.palavramestra.data.local.entity.WordEntity
import com.palavramestra.ui.viewmodel.AdminViewModel
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onAddWord: () -> Unit,
    onEditWord: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val factory = (context.applicationContext as PalavraMestraApp).container.viewModelFactory
    val viewModel: AdminViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity,
        factory = factory
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel do Admin") },
                actions = { OutlinedButton(onClick = onLogout) { Text("Sair") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onAddWord, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.padding(end = 4.dp))
                    Text("Nova Palavra")
                }
                OutlinedButton(onClick = { viewModel.syncFromCloud() }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CloudSync, contentDescription = null)
                    Spacer(Modifier.padding(end = 4.dp))
                    Text("Sincronizar Nuvem")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "A nuvem só busca palavras em inglês. Elas não entram no jogo até você confirmar.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (uiState.words.any { it.category == "API" }) {
                TextButton(onClick = { viewModel.deleteCloudWords() }) {
                    Text("Remover palavras da internet")
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                label = { Text("Buscar palavra ou categoria") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.selectedDifficulty == null,
                    onClick = { viewModel.updateDifficultyFilter(null) },
                    label = { Text("Todas") }
                )
                listOf("FACIL", "MEDIO", "DIFICIL").forEach { diff ->
                    FilterChip(
                        selected = uiState.selectedDifficulty == diff,
                        onClick = { viewModel.updateDifficultyFilter(diff) },
                        label = { Text(diff) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            if (uiState.pendingCloudWords.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { viewModel.discardCloudWords() },
                    title = { Text("Palavras da nuvem") },
                    text = {
                        Text(
                            "A API devolveu palavras em inglês. Elas não fazem parte do jogo em português.\n\n" +
                                uiState.pendingCloudWords.joinToString(", ") +
                                "\n\nQuer adicionar mesmo assim?"
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmCloudWords() }) { Text("Adicionar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.discardCloudWords() }) { Text("Descartar") }
                    }
                )
            }

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
            }
            uiState.successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.filteredWords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma palavra encontrada.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.filteredWords) { word ->
                        WordItem(
                            word = word,
                            onEdit = { onEditWord(word.id) },
                            onDelete = { viewModel.deleteWord(word) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordItem(word: WordEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(word.word, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${word.category} • ${word.difficulty}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Deletar") }
        }
    }
}
