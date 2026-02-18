package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModel

@Composable
fun DashboardScreen(
    mealsVm: MealsViewModel,
    waterVm: WaterViewModel,
    healthConnectVm: HealthConnectViewModel,
    onAddManual: () -> Unit,
    onScan: () -> Unit,
    onProgress: () -> Unit,
    onMenu: () -> Unit,
    onPlanner: () -> Unit,
) {
    val context = LocalContext.current

    // Water from Room
    val todayWaterTotal by waterVm.todayTotalMl.collectAsState(initial = 0)

    // Steps from HealthConnect VM (mutableStateOf)
    val steps = healthConnectVm.steps.value

    // Load steps on start (will show 0 if not available or no permission)
    LaunchedEffect(Unit) {
        healthConnectVm.initialLoad()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)

        // ✅ Quick actions
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onScan, modifier = Modifier.weight(1f)) { Text("Scan") }
                    OutlinedButton(onClick = onAddManual, modifier = Modifier.weight(1f)) { Text("Manual") }
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onMenu, modifier = Modifier.weight(1f)) { Text("Menu") }
                    OutlinedButton(onClick = onPlanner, modifier = Modifier.weight(1f)) { Text("Plan") }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(onClick = onProgress, modifier = Modifier.fillMaxWidth()) {
                    Text("Progress")
                }
            }
        }

        // ✅ Water tracker (Room + ViewModel)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Water", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("Today: $todayWaterTotal ml")

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { waterVm.add(250) }, modifier = Modifier.weight(1f)) {
                        Text("+250 ml")
                    }
                    OutlinedButton(onClick = { waterVm.add(500) }, modifier = Modifier.weight(1f)) {
                        Text("+500 ml")
                    }
                }
            }
        }

        // ✅ Steps (safe placeholder without PermissionController)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Steps", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("Today: $steps steps")

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { healthConnectVm.initialLoad() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refresh")
                    }

                    OutlinedButton(
                        onClick = { healthConnectVm.openHealthConnect(context) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open HC")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "If steps stay 0: install Health Connect + grant permission inside Health Connect settings.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
