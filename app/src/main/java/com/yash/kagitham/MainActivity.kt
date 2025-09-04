package com.yash.kagitham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yash.kagitham.ui.theme.KagithamTheme
import com.yash.sdk.AppRegistry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the SDK Registry with app context
        AppRegistry.setAppContext(applicationContext)

        enableEdgeToEdge()
        setContent {
            KagithamTheme {
                NavigationMain()
            }
        }
    }
}
