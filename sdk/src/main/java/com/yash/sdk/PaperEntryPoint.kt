package com.yash.sdk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text


import androidx.navigation.NavController

abstract class PaperEntryPoint {

    @Composable
    abstract fun PluginContent()

    @Composable
    fun RenderWithHome() {
        val navController: NavController? = AppRegistry.getNavController()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Button(
                onClick = { navController?.navigate("App") },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text("\uD83C\uDFE0 Home")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp), // leave space so plugin UI doesn’t overlap the button
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                PluginContent()
            }
        }
    }

}