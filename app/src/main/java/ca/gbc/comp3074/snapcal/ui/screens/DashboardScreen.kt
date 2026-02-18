package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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

    val mealState = mealsVm.uiState.collectAsState().value
    val todayWaterTotal by waterVm.todayTotalMl.collectAsState(initial = 0)
    val steps by healthConnectVm.steps

    LaunchedEffect(Unit) {
        // Don’t crash if permission isn’t granted; your VM handles it
        healthConnectVm.refreshSteps()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Today overview",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ✅ Calories summary
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Calories Today",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${mealState.todayCalories} kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = onProgress, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.ShowChart, contentDescription = null)
                            Text("Progress", modifier = Modifier.padding(start = 8.dp))
                        }
                        OutlinedButton(onClick = onPlanner, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Today, contentDescription = null)
                            Text("Plan", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }

        // ✅ Water + Steps
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocalDrink, contentDescription = null)
                            Text("Water", style = MaterialTheme.typography.titleMedium)
                        }

                        Text("$todayWaterTotal ml", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { waterVm.add(250) }, modifier = Modifier.weight(1f)) { Text("+250") }
                            OutlinedButton(onClick = { waterVm.add(500) }, modifier = Modifier.weight(1f)) { Text("+500") }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Steps", style = MaterialTheme.typography.titleMedium)
                        Text("$steps", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            if (healthConnectVm.isAvailable.value) "Health Connect" else "Not available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                if (!healthConnectVm.isAvailable.value) {
                                    healthConnectVm.openHealthConnect(context)
                                } else {
                                    healthConnectVm.refreshSteps()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (!healthConnectVm.isAvailable.value) "Install / Open" else "Refresh")
                        }
                    }
                }
            }
        }

        // ✅ Quick actions
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Quick Actions", style = MaterialTheme.typography.titleMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onScan, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Text("Scan", modifier = Modifier.padding(start = 8.dp))
                        }
                        OutlinedButton(onClick = onAddManual, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Restaurant, contentDescription = null)
                            Text("Manual", modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    OutlinedButton(onClick = onMenu, modifier = Modifier.fillMaxWidth()) {
                        Text("Browse Healthy Menu")
                    }
                }
            }
        }

        // ✅ Recent meals
        item {
            Text("Recent Meals", style = MaterialTheme.typography.titleMedium)
            Divider()
        }

        if (mealState.meals.isEmpty()) {
            item {
                Text(
                    "No meals yet. Add one manually or scan a label.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(mealState.meals.take(10)) { meal ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            meal.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${meal.calories} kcal • P ${meal.protein} • C ${meal.carbs} • F ${meal.fat}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
