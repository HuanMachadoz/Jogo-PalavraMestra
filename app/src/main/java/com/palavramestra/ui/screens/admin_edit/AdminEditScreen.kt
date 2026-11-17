package com.palavramestra.ui.screens.admin_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palavramestra.PalavraMestraApp
import com.palavramestra.ui.viewmodel.AdminViewModel
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditScreen(
    wordId: Int?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val factory = (context.applicationContext as PalavraMestraApp).container.viewModelFactory
    val viewModel: AdminViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity,
        factory = factory
    )
    val uiState by viewModel.uiState.collectAsState()

    var word by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("FACIL") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(wordId, uiState.words) {
        if (wordId != null) {
            val found = uiState.words.find { it.id == wordId }
            if (found != null) {
                word = found.word
                category = found.category
                difficulty = found.difficulty
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (wordId == null) "Nova Palavra" else "Editar Palavra") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("Palavra") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoria (ex: FRUTA, ANIMAL)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Text("Dificuldade", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("FACIL", "MEDIO", "DIFICIL").forEach { diff ->
                    val selected = difficulty == diff
                    if (selected) {
                        Button(onClick = { difficulty = diff }) { Text(diff) }
                    } else {
                        OutlinedButton(onClick = { difficulty = diff }) { Text(diff) }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        word.isBlank() -> error = "Digite a palavra."
                        category.isBlank() -> error = "Digite a categoria."
                        else -> {
                            error = null
                            if (wordId == null) {
                                viewModel.addWord(word, category, difficulty)
                            } else {
                                val existing = uiState.words.find { it.id == wordId }
                                if (existing != null) {
                                    viewModel.updateWord(existing, word, category, difficulty)
                                }
                            }
                            onSaved()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Salvar", fontSize = 16.sp) }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }
    }
}
