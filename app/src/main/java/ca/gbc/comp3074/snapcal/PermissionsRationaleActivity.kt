package ca.gbc.comp3074.snapcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Permissions Rationale",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "We need access to your health data to provide you with personalized insights and track your progress.",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
