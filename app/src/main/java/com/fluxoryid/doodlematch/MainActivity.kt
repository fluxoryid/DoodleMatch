package com.fluxoryid.doodlematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fluxoryid.doodlematch.navigation.DoodleMatchNavHost
import com.fluxoryid.doodlematch.ui.theme.DoodleMatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoodleMatchTheme {
                DoodleMatchNavHost()
            }
        }
    }
}
