package com.aulasandroid.pokedex.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aulasandroid.pokedex.R
import com.aulasandroid.pokedex.model.PokemonDto
import com.aulasandroid.pokedex.model.displayId
import com.aulasandroid.pokedex.model.displayName
import com.aulasandroid.pokedex.model.imageUrl
import com.aulasandroid.pokedex.model.mainColor
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aulasandroid.pokedex.components.PokemonImage

@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vm: HomeViewModel = viewModel()
) {
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
                    onClick = { onPokemonClick(pokemon.id) }
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