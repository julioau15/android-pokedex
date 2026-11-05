package com.aulasandroid.pokedex.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun PokemonImage(modifier: Modifier = Modifier, url: String?) {
    AsyncImage(
        model = url,
        contentDescription = "Imagem do Pokémon",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}