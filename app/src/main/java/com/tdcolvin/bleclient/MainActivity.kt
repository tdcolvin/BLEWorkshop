package com.tdcolvin.bleclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tdcolvin.bleclient.ui.navigation.MainNavigation
import com.tdcolvin.bleclient.ui.theme.BLEClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLEClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.systemBarsPadding().fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   MainNavigation(
                       modifier = Modifier.fillMaxSize()
                   )
                }
            }
        }
    }
}