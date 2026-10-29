package com.aulasandroid.pokedex

import android.R.attr.label
import android.R.attr.name
import android.R.attr.text
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aulasandroid.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var idOrName by remember {
        mutableStateOf("")
    }

    Column(
        modifier
            .fillMaxSize()
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
            // imagem
            Image(
                painterResource(R.drawable.pokeboll_icon),
                contentDescription = "logo quiz",
                modifier = Modifier
                    .size(20.dp)
            )

            // texto: POKÉDEX
            Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = "Pokédex",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

        }

        // barra pesquisa
        OutlinedTextField(
            value = idOrName,
            onValueChange = { idOrName = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            label = { Text( text = "Nome ou ID" ) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters
            ),
            trailingIcon = {
                IconButton(
                    onClick = {}
                ){
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = ""
                    )
                }
            }
        )

        // cards result
        LazyColumn(

        ) {
            items(10){
                CardPokemon()
            }
        }
    }
}

// card do pokemon
@Composable
fun CardPokemon (){
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .background(Color.White),
        border = BorderStroke(2.dp, Color.Green)
    ) {
        Text(
            text = "#001",
            textAlign = TextAlign.End,
            color = Color.Green
        )

        PokemonImage(
            modifier = Modifier
                .weight(1f),
            url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
        ) {
            Text(
                text = "Bulbassaur"
            )
        }
    }
}

// tela de detalhes do pokemon
@Composable
fun InformationScreen (){
    Column(

    ) {
        // header / voltar / nome personagem / cod

        // tela de fundo

        // imagem personagem
        PokemonImage(
            modifier = Modifier
                .size(100.dp)
            ,
            url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png"
        )

        // card informacao do personagem
        CardInformacaoPokemon()
    }
}

// card informacao personagem
@Composable
fun CardInformacaoPokemon (){
    Card(

    ) {

    }
}

// imagem personagem
@Composable
fun PokemonImage(modifier: Modifier = Modifier, url: String?) {
    AsyncImage(
        model = url,
        contentDescription = "Imagem do Pokémon",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}