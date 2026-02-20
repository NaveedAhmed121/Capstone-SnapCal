package ca.gbc.comp3074.snapcal

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * This activity is used to show the rationale for Health Connect permissions.
 * It is required by the Health Connect API.
 */
class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This activity does not need a UI.
        // It is used to show the rationale for Health Connect permissions.
        finish()
    }
}
