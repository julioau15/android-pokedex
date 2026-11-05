package com.aulasandroid.pokedex.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aulasandroid.pokedex.components.PokemonImage
import com.aulasandroid.pokedex.model.PokemonDto
import com.aulasandroid.pokedex.model.PokemonType
import com.aulasandroid.pokedex.model.displayId
import com.aulasandroid.pokedex.model.displayName
import com.aulasandroid.pokedex.model.imageUrl
import com.aulasandroid.pokedex.model.mainColor

@Composable
fun DetailScreen(
    id: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: DetailViewModel = viewModel()
) {
    LaunchedEffect(id) { vm.load(id) }

    val pokemon = vm.pokemon
    if (pokemon == null || pokemon.id != id) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = vm.message)
        }
    } else {
        DetailContent(pokemon, onBack, modifier)
    }
}

@Composable
private fun DetailContent(pokemon: PokemonDto, onBack: () -> Unit, modifier: Modifier = Modifier) {
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