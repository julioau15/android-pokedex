package com.aulasandroid.pokedex.model

import androidx.compose.ui.graphics.Color

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