package com.yash.kagitham.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        Triple("Widgets", Icons.Filled.Home, "home"),
        Triple("Paper", Icons.Filled.Extension, "plugin"),
        Triple("Install", Icons.Filled.Download, "install"),
//        Triple("Activities", Icons.Default.AccessTime,"Activities")
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(25.dp))
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(0.5.dp, Color.White, RoundedCornerShape(25.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(50.dp) // full row height
                        .weight(1f) // each takes equal width
                        .clip(RoundedCornerShape(60.dp))
                        .clickable { onItemSelected(index) } // clickable on whole row item
                        .background(
                            Color.Black,
                            RoundedCornerShape(60.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(30.dp),
                        tint = if (selectedIndex == index) Color(0xFFFF3B3B) else Color.Gray,
                    )
                }

                if (index != items.lastIndex) {
                    Spacer(modifier = Modifier.width(32.dp))
                }
            }
        }
    }
}
