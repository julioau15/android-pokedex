package com.aulasandroid.pokedex.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

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

// funções auxiliares para facilitar o uso na tela
fun PokemonDto.mainColor(): Color =
    PokemonType.fromApi(types.first().type.name).color

fun PokemonDto.imageUrl() =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/$id.png"

fun PokemonDto.displayName() = name.replaceFirstChar { it.uppercase() }

fun PokemonDto.displayId() = "#" + id.toString().padStart(3, '0')