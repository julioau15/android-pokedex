package com.aulasandroid.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aulasandroid.pokedex.ui.theme.PokedexTheme
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ---------- MODEL (dados da API) ----------

data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val abilities: List<AbilitySlot>,
    val stats: List<StatSlot>
)

data class NamedResource(val name: String)
data class TypeSlot(val type: NamedResource)
data class AbilitySlot(val ability: NamedResource)
data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResource
)

interface PokeApi {
    // serve para ID ou nome: pokemon/1 ou pokemon/pikachu
    @GET("pokemon/{idOrName}")
    suspend fun getPokemon(@Path("idOrName") idOrName: String): PokemonDto
}

object Api {
    val service: PokeApi = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PokeApi::class.java)
}

// ---------- ENUM de cores por tipo ----------

enum class PokemonType(val apiName: String, val color: Color) {
    NORMAL("normal", Color(0xFFA8A878)),
    FIRE("fire", Color(0xFFE8833A)),
    WATER("water", Color(0xFF6890F0)),
    ELECTRIC("electric", Color(0xFFF8D030)),
    GRASS("grass", Color(0xFF8BC85C)),
    ICE("ice", Color(0xFF98D8D8)),
    FIGHTING("fighting", Color(0xFFC03028)),
    POISON("poison", Color(0xFF94459B)),
    GROUND("ground", Color(0xFFE0C068)),
    FLYING("flying", Color(0xFFA890F0)),
    PSYCHIC("psychic", Color(0xFFF85888)),
    BUG("bug", Color(0xFFA8B820)),
    ROCK("rock", Color(0xFFB8A038)),
    GHOST("ghost", Color(0xFF705898)),
    DRAGON("dragon", Color(0xFF7038F8)),
    DARK("dark", Color(0xFF705848)),
    STEEL("steel", Color(0xFFB8B8D0)),
    FAIRY("fairy", Color(0xFFEE99AC));

    companion object {
        fun fromApi(name: String): PokemonType {
            return entries.firstOrNull { it.apiName == name } ?: NORMAL
        }
    }
}

fun PokemonDto.mainColor(): Color =
    PokemonType.fromApi(types.first().type.name).color

fun PokemonDto.imageUrl() =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/$id.png"

fun PokemonDto.displayName() = name.replaceFirstChar { it.uppercase() }

fun PokemonDto.displayId() = "#" + id.toString().padStart(3, '0')

// ---------- VIEWMODEL ----------

class PokedexViewModel : ViewModel() {

    var pokemons by mutableStateOf<List<PokemonDto>>(emptyList())
        private set

    var selected by mutableStateOf<PokemonDto?>(null)

    var message by mutableStateOf("")
        private set

    private var all: List<PokemonDto> = emptyList()

    init {
        loadFirst()
    }

    private fun loadFirst() {
        viewModelScope.launch {
            try {
                val list = mutableListOf<PokemonDto>()
                for (id in 1..30) {
                    list.add(Api.service.getPokemon(id.toString()))
                    all = list.toList()
                    pokemons = all
                }
            } catch (e: Exception) {
                message = "Erro ao carregar. Verifique a internet."
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            pokemons = all
            message = ""
            return
        }
        viewModelScope.launch {
            try {
                pokemons = listOf(Api.service.getPokemon(query.trim().lowercase()))
                message = ""
            } catch (e: Exception) {
                pokemons = emptyList()
                message = "Pokémon não encontrado"
            }
        }
    }
}

// ---------- VIEW ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val vm: PokedexViewModel = viewModel()
                    val selected = vm.selected

                    if (selected == null) {
                        HomeScreen(vm, Modifier.padding(innerPadding))
                    } else {
                        BackHandler { vm.selected = null }
                        DetailScreen(
                            pokemon = selected,
                            onBack = { vm.selected = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(vm: PokedexViewModel, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // header
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Red)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.pokeboll_icon),
                contentDescription = "logo pokédex",
                modifier = Modifier.size(20.dp)
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Pokédex",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        // barra de pesquisa
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            label = { Text(text = "Nome ou ID") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { vm.search(query) }),
            trailingIcon = {
                IconButton(onClick = { vm.search(query) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                }
            }
        )

        if (vm.message.isNotEmpty()) {
            Text(
                text = vm.message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        // grade de cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.pokemons) { pokemon ->
                CardPokemon(
                    pokemon = pokemon,
                    onClick = { vm.selected = pokemon }
                )
            }
        }
    }
}

@Composable
fun CardPokemon(pokemon: PokemonDto, onClick: () -> Unit) {
    val color = pokemon.mainColor()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, color),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = pokemon.displayId(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 8.dp),
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = color
            )

            PokemonImage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                url = pokemon.imageUrl()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pokemon.displayName(),
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DetailScreen(pokemon: PokemonDto, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val color = pokemon.mainColor()

    Column(
        modifier
            .fillMaxSize()
            .background(color)
    ) {
        // header: voltar, nome e código
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            Text(
                text = pokemon.displayName(),
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = pokemon.displayId(),
                modifier = Modifier.padding(end = 8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // imagem
        PokemonImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            url = pokemon.imageUrl()
        )

        // card branco com as informações
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // tipos
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pokemon.types.forEach { slot ->
                    val type = PokemonType.fromApi(slot.type.name)
                    Text(
                        text = slot.type.name.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(type.color, RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }

            // About
            Text(
                text = "About",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem("${pokemon.weight / 10.0} kg", "Weight")
                InfoItem("${pokemon.height / 10.0} m", "Height")
                InfoItem(
                    pokemon.abilities.joinToString("\n") { it.ability.name.replaceFirstChar { c -> c.uppercase() } },
                    "Moves"
                )
            }

            // Base Stats
            Text(
                text = "Base Stats",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            pokemon.stats.forEach { stat ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statLabel(stat.stat.name),
                        modifier = Modifier.width(48.dp),
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = stat.baseStat.toString().padStart(3, '0'),
                        modifier = Modifier.width(40.dp),
                        fontSize = 12.sp
                    )
                    LinearProgressIndicator(
                        progress = { stat.baseStat / 255f },
                        modifier = Modifier.fillMaxWidth(),
                        color = color,
                        trackColor = color.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 14.sp, textAlign = TextAlign.Center)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

fun statLabel(apiName: String): String = when (apiName) {
    "hp" -> "HP"
    "attack" -> "ATK"
    "defense" -> "DEF"
    "special-attack" -> "SATK"
    "special-defense" -> "SDEF"
    "speed" -> "SPD"
    else -> apiName
}

@Composable
fun PokemonImage(modifier: Modifier = Modifier, url: String?) {
    AsyncImage(
        model = url,
        contentDescription = "Imagem do Pokémon",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}