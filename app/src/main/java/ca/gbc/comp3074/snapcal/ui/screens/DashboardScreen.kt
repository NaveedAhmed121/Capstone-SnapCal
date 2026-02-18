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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModel

@Composable
fun DashboardScreen(
    mealsVm: MealsViewModel,
    waterVm: WaterViewModel,
    onAddManual: () -> Unit,
    onScan: () -> Unit,
    onProgress: () -> Unit,
    onMenu: () -> Unit,
    onPlanner: () -> Unit,
) {
    val todayWaterTotal by waterVm.todayTotalMl.collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)

        // Calories / Meals quick summary (simple for demo)
        Card {
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

        // Water (demo placeholder — we’ll connect to Room next)
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Water", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("Today: ${todayWaterTotal ?: 0} ml")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { waterVm.add(250) }) { Text("+250") }
                    OutlinedButton(onClick = { waterVm.add(500) }) { Text("+500") }
                }
            }
        }

        // Steps (demo placeholder — can connect to Health Connect later)
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Steps", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text("Today: 0 steps")
            }
        }
    }
}
