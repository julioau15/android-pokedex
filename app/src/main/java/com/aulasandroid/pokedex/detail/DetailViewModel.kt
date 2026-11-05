package com.aulasandroid.pokedex.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aulasandroid.pokedex.model.Api
import com.aulasandroid.pokedex.model.PokemonDto
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    var pokemon by mutableStateOf<PokemonDto?>(null)
        private set

    var message by mutableStateOf("Carregando...")
        private set

    fun load(id: Int) {
        message = "Carregando..."
        viewModelScope.launch {
            try {
                pokemon = Api.service.getPokemon(id.toString())
            } catch (e: Exception) {
                message = "Erro ao carregar. Verifique a internet."
            }
        }
    }
}