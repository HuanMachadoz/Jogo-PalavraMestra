package com.palavramestra.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palavramestra.di.ViewModelFactory
import com.palavramestra.ui.theme.GreenCorrect
import com.palavramestra.ui.theme.RedWrong
import com.palavramestra.ui.viewmodel.GameState
import com.palavramestra.ui.viewmodel.GameViewModel
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    playerName: String,
    onGoToSettings: () -> Unit,
    onGoToRanking: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val factory = (context.applicationContext as com.palavramestra.PalavraMestraApp)
        .container.viewModelFactory
    val viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity,
        factory = factory
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jogando: ${uiState.playerName.ifBlank { playerName }}") },
                actions = {
                    OutlinedButton(onClick = onLogout) { Text("Sair") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.gameStarted) {
                StartGameView(playerName, viewModel, onGoToRanking)
                return@Column
            }

            Text(
                "Dificuldade: ${uiState.difficulty.label}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            Row {
                repeat(uiState.maxAttempts) { index ->
                    val alive = index < uiState.attemptsLeft
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (alive) GreenCorrect else RedWrong)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = uiState.displayWord,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            Text("Erradas:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                uiState.wrongLetters.joinToString(" "),
                fontSize = 20.sp,
                color = RedWrong,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            when (uiState.gameState) {
                GameState.WON -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GreenCorrect)
                        Spacer(Modifier.width(8.dp))
                        Text("Você venceu!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GreenCorrect)
                    }
                    Text("Pontuação: ${uiState.score}", fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.startGame(uiState.difficulty, playerName) }) {
                        Text("Jogar Novamente")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onGoToRanking) { Text("Ver Ranking") }
                }
                GameState.LOST -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SentimentDissatisfied, contentDescription = null, tint = RedWrong)
                        Spacer(Modifier.width(8.dp))
                        Text("Você perdeu!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = RedWrong)
                    }
                    Text("A palavra era: ${uiState.currentWord}", fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.startGame(uiState.difficulty, playerName) }) {
                        Text("Tentar Novamente")
                    }
                }
                GameState.PLAYING -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val alphabet = ('A'..'Z').toList()
                        items(alphabet) { letter ->
                            val used = uiState.guessedLetters.contains(letter)
                            Button(
                                onClick = { viewModel.guessLetter(letter) },
                                enabled = !used,
                                modifier = Modifier.aspectRatio(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (used) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    letter.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            uiState.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun StartGameView(
    playerName: String,
    viewModel: GameViewModel,
    onGoToRanking: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(40.dp))
        Text("Olá, $playerName!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Escolha a dificuldade e comece a jogar:", fontSize = 16.sp)
        Spacer(Modifier.height(24.dp))

        com.palavramestra.ui.viewmodel.Difficulty.values().forEach { diff ->
            Button(
                onClick = { viewModel.startGame(diff, playerName) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("${diff.label} (${diff.maxAttempts} tentativas)")
            }
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onGoToRanking) {
            Text("Ver Ranking")
        }
    }
}
