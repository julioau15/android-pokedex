package com.aulasandroid.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aulasandroid.pokedex.detail.DetailScreen
import com.aulasandroid.pokedex.home.HomeScreen
import com.aulasandroid.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // id do pokémon aberto (null = está na tela inicial)
                    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
                    val id = selectedId

                    if (id == null) {
                        HomeScreen(
                            onPokemonClick = { selectedId = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        BackHandler { selectedId = null }
                        DetailScreen(
                            id = id,
                            onBack = { selectedId = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}