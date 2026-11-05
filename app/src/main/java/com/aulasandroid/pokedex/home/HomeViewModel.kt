package com.aulasandroid.pokedex.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aulasandroid.pokedex.model.Api
import com.aulasandroid.pokedex.model.PokemonDto
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var pokemons by mutableStateOf<List<PokemonDto>>(emptyList())
        private set

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