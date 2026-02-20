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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    vm: WaterViewModel,
    modifier: Modifier = Modifier
) {
    // ✅ Goal can be changed; not persisted yet (easy version)
    var goalMl by remember { mutableIntStateOf(2000) }
    var customMlText by remember { mutableStateOf("") }

    // ✅ Explicit types prevent "Cannot infer type parameter" errors
    val todayMl: Int by vm.todayTotalMl.collectAsStateWithLifecycle(initialValue = 0)
    val entries: List<WaterEntry> by vm.entries.collectAsStateWithLifecycle(initialValue = emptyList())

    val progress = if (goalMl <= 0) 0f else (todayMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)
    val progressPct = (progress * 100f).roundToInt()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Water Tracker") },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        )

        // Top content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null)
                        Text("Today", style = MaterialTheme.typography.titleMedium)
                    }

                    Text("$todayMl ml", style = MaterialTheme.typography.headlineMedium)

                    Text(
                        "Goal: $goalMl ml  •  $progressPct%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ✅ Goal controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { if (goalMl > 500) goalMl -= 250 },
                            modifier = Modifier.weight(1f)
                        ) { Text("-250 goal") }

                        OutlinedButton(
                            onClick = { goalMl += 250 },
                            modifier = Modifier.weight(1f)
                        ) { Text("+250 goal") }
                    }
                }
            }

            // Quick add buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.add(250) }, modifier = Modifier.weight(1f)) { Text("+250") }
                Button(onClick = { vm.add(500) }, modifier = Modifier.weight(1f)) { Text("+500") }
                Button(onClick = { vm.add(1000) }, modifier = Modifier.weight(1f)) { Text("+1000") }
            }

            // Custom add row
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

            Spacer(Modifier.height(4.dp))
            Text("History", style = MaterialTheme.typography.titleMedium)
        }

        // History list
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
                            Text(
                                formatTime(entry.createdAt),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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