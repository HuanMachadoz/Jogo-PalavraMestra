# Palavra Mestra

Jogo da forca para Android. O jogador escolhe a dificuldade, tenta adivinhar a palavra e a pontuação fica salva no ranking. O administrador entra com senha e gerencia as palavras do jogo.

Feito em Kotlin com Jetpack Compose e arquitetura MVVM. Os dados locais ficam no Room. A busca de palavras na internet usa Retrofit. 

Feito por: Huan Machado, Felipe Gabriel da Silva

## Como jogar

1. Na tela de login, digite seu nome e toque em **Entrar como Jogador**.
2. Escolha a dificuldade: Fácil (8 tentativas), Médio (6) ou Difícil (4).
3. Toque nas letras para adivinhar a palavra.
4. Se acertar a palavra, a pontuação entra no ranking.

A barra de baixo leva para **Jogar**, **Config** e **Ranking**.

## Modo administrador

Senha: `admin123`

No campo de senha, digite `admin123` e toque em **Entrar como Administrador**. Dá para:

- adicionar, editar e apagar palavras
- buscar por nome ou categoria
- filtrar por dificuldade
- buscar palavras em inglês numa API externa

O botão **Sincronizar Nuvem** só mostra as palavras. Elas só entram no jogo se você confirmar. **Remover palavras da internet** apaga as que vieram da API.

## Telas

1. Login
2. Jogo
3. Configurações
4. Ranking
5. Painel do administrador
6. Adicionar ou editar palavra

## Arquitetura

| Pacote | Função |
|---|---|
| `ui` | Telas, navegação e ViewModels |
| `data.local` | Room: entidades, DAOs e banco |
| `data.repository` | Acesso aos dados e tratamento de erro |
| `data.remote` | Retrofit e a API de palavras |
| `di` | `ViewModelFactory`, que injeta os repositórios |

Dois ViewModels, cada um com o próprio `UiState`:

- `GameViewModel`: partida, tentativas, pontuação e ranking
- `AdminViewModel`: CRUD das palavras, filtro e sincronização

Duas tabelas no Room:

- `words`: palavra, categoria e dificuldade
- `ranking`: nome do jogador, pontos e tempo

## Como rodar

1. Abra a pasta do projeto no Android Studio.
2. Espere o Gradle sincronizar.
3. Rode em um emulador ou celular com Android 7.0 (API 24) ou mais recente.

Para gerar o APK de debug, na pasta do projeto:

```bash
gradlew.bat assembleDebug
```

O arquivo fica em `app/build/outputs/apk/debug/app-debug.apk`. 


https://youtube.com/shorts/RVltLvRZHUY?feature=share




 
<img width="456" height="726" alt="image" src="https://github.com/user-attachments/assets/6f4b165d-0056-45c4-b7bd-b5b5ab03eddb" /> 

<img width="449" height="599" alt="image" src="https://github.com/user-attachments/assets/d0e3f6f4-8677-47ca-9771-1e30691998b2" />

<img width="453" height="635" alt="image" src="https://github.com/user-attachments/assets/bf243bef-5fcd-4886-9bd5-27f0c76f9699" />

<img width="445" height="867" alt="image" src="https://github.com/user-attachments/assets/bc6c51d7-97f7-4ce3-b1bc-a3d799d7eb51" />

 <img width="455" height="859" alt="image" src="https://github.com/user-attachments/assets/4df8486d-346a-4692-9666-f93b7d86114a" />

 <img width="457" height="561" alt="image" src="https://github.com/user-attachments/assets/eb810d46-dfe4-4711-93c3-d9686de1b48e" />

