package com.palavramestra.ui.screens.ranking

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.palavramestra.data.local.entity.RankingEntity
import com.palavramestra.ui.viewmodel.GameViewModel
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import com.palavramestra.PalavraMestraApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onBackToGame: () -> Unit
) {
    val context = LocalContext.current
    val factory = (context.applicationContext as PalavraMestraApp).container.viewModelFactory
    val viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity,
        factory = factory
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ranking") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (uiState.ranking.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.padding(8.dp))
                        Text("Nenhuma pontuação ainda.", fontSize = 18.sp)
                        Text("Jogue para aparecer no ranking!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.ranking) { item ->
                        RankingItem(item) { viewModel.deleteRanking(it) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onBackToGame, modifier = Modifier.fillMaxWidth()) {
                Text("Voltar ao Jogo")
            }
        }
    }
}

@Composable
private fun RankingItem(item: RankingEntity, onDelete: (RankingEntity) -> Unit) {
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
                Text(item.playerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Pontos: ${item.score} • Tempo: ${item.timeSeconds}s",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar")
            }
        }
    }
}
