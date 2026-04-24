package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.gbc.comp3074.snapcal.data.model.WaterEntry
import ca.gbc.comp3074.snapcal.ui.theme.*
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    vm: WaterViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    var goalMl by remember { mutableIntStateOf(2000) }
    var customMlText by remember { mutableStateOf("") }

    val todayMl: Int by vm.todayTotalMl.collectAsStateWithLifecycle(initialValue = 0)
    val entries: List<WaterEntry> by vm.entries.collectAsStateWithLifecycle(initialValue = emptyList())

    val progress = if (goalMl <= 0) 0f else (todayMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)
    val progressPct = (progress * 100f).roundToInt()

    Column(modifier = modifier.fillMaxSize()) {
        // Pink gradient header to match other screens
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        "💧 Water Tracker",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Progress Overview Card in Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.92f))
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(60.dp),
                                color = BlueAccent,
                                strokeWidth = 6.dp,
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                            Text("${progressPct}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("$todayMl / $goalMl ml", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PinkDark)
                            Text("Daily Intake Goal", style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Goal Controls
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🎯 Set Your Goal", style = MaterialTheme.typography.titleSmall, color = PinkDark, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (goalMl > 500) goalMl -= 250 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("-250 ml") }
                            
                            Text("$goalMl ml", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            
                            OutlinedButton(
                                onClick = { goalMl += 250 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("+250 ml") }
                        }
                    }
                }
            }

            // Quick Add
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("➕ Quick Log", style = MaterialTheme.typography.titleSmall, color = PinkDark, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { vm.add(250) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BlueAccent), shape = RoundedCornerShape(10.dp)) { Text("250ml") }
                            Button(onClick = { vm.add(500) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BlueAccent), shape = RoundedCornerShape(10.dp)) { Text("500ml") }
                            Button(onClick = { vm.add(1000) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BlueAccent), shape = RoundedCornerShape(10.dp)) { Text("1L") }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = customMlText,
                                onValueChange = { customMlText = it.filter(Char::isDigit).take(4) },
                                label = { Text("Custom Amount") },
                                suffix = { Text("ml") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    val ml = customMlText.toIntOrNull() ?: return@Button
                                    if (ml > 0) vm.add(ml)
                                    customMlText = ""
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                            ) { Text("Add") }
                        }
                    }
                }
            }

            // History
            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            if (entries.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text(
                            "No water entries yet. Stay hydrated!",
                            modifier = Modifier.padding(16.dp),
                            color = SubtleGray
                        )
                    }
                }
            } else {
                items(entries.take(10)) { entry ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("💧", fontSize = 20.sp)
                                Column {
                                    Text("${entry.amountMl} ml", fontWeight = FontWeight.SemiBold)
                                    Text(formatTime(entry.createdAt), style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                                }
                            }
                            Icon(Icons.Default.LocalDrink, null, tint = BlueAccent.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(ms))
}
