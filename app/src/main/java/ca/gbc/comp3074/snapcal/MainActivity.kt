package ca.gbc.comp3074.snapcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ca.gbc.comp3074.snapcal.ui.navigation.SnapCalApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SnapCalApp() }
    }
}
