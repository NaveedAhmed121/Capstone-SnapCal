package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.gbc.comp3074.snapcal.data.model.WaterEntry
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    vm: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val todayMlState = vm.todayTotalMl.collectAsStateWithLifecycle(initialValue = 0)
    val entriesState = vm.entries.collectAsStateWithLifecycle(initialValue = emptyList<WaterEntry>())

    val todayMl = todayMlState.value
    val entries = entriesState.value

    var customMlText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Water Tracker") },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null)
                        Text("Today", style = MaterialTheme.typography.titleMedium)
                    }

                    Text("$todayMl ml", style = MaterialTheme.typography.headlineMedium)

                    Text(
                        "Tip: 2000 ml/day is a common goal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.add(250) }, modifier = Modifier.weight(1f)) { Text("+250") }
                Button(onClick = { vm.add(500) }, modifier = Modifier.weight(1f)) { Text("+500") }
                Button(onClick = { vm.add(1000) }, modifier = Modifier.weight(1f)) { Text("+1000") }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = customMlText,
                    onValueChange = { customMlText = it.filter(Char::isDigit).take(4) },
                    label = { Text("Custom (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = {
                        val ml = customMlText.toIntOrNull() ?: return@OutlinedButton
                        if (ml > 0) vm.add(ml)
                        customMlText = ""
                    }
                ) { Text("Add") }
            }

            Text("History", style = MaterialTheme.typography.titleMedium)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (entries.isEmpty()) {
                item {
                    Text(
                        "No water entries yet. Add some using the buttons above.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(entries) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${entry.amountMl} ml", style = MaterialTheme.typography.titleMedium)
                            Text(formatTime(entry.createdAt), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return sdf.format(Date(ms))
}
