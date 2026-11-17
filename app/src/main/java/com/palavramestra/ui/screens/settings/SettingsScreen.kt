package com.palavramestra.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palavramestra.ui.viewmodel.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    onBackToGame: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Configurações") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Dificuldade Padrão", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Escolha a dificuldade que será usada ao iniciar novas partidas.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Difficulty.values().forEach { diff ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (diff == selectedDifficulty),
                            onClick = { onDifficultyChange(diff) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (diff == selectedDifficulty),
                        onClick = { onDifficultyChange(diff) }
                    )
                    Spacer(Modifier.padding(start = 8.dp))
                    Column {
                        Text(diff.label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "${diff.maxAttempts} tentativas",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Como Jogar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("1. Escolha uma dificuldade.")
                    Text("2. Uma palavra secreta é sorteada.")
                    Text("3. Clique nas letras para adivinhar.")
                    Text("4. Letras erradas gastam uma tentativa.")
                    Text("5. Acerte a palavra inteira para vencer!")
                    Text("6. Sua pontuação vai para o Ranking.")
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onBackToGame, modifier = Modifier.fillMaxWidth()) {
                Text("Voltar ao Jogo")
            }
        }
    }
}
