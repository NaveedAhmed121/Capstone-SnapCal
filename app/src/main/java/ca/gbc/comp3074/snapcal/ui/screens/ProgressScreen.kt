package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.viewmodel.ProgressViewModel
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    progressVm: ProgressViewModel,
    healthConnectVm: HealthConnectViewModel
) {
    val caloriesData = progressVm.lastNDaysCaloriesFilled(7).collectAsState(initial = emptyList()).value
    val waterData = progressVm.lastNDaysWaterFilled(7).collectAsState(initial = emptyList()).value
    var stepsData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val newStepsData = (0..6).associate {
            val date = today.minusDays(it.toLong())
            val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            date.toString() to healthConnectVm.readStepsByDate(context, start, end)
        }
        stepsData = newStepsData
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Last 7 days calories", style = MaterialTheme.typography.titleMedium)

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        if (caloriesData.isEmpty()) {
                            Text("No data yet. Add meals to see progress.")
                        } else {
                            BarChart(
                                labels = caloriesData.map { it.day.substring(5) }, // MM-dd
                                values = caloriesData.map { it.calories.toFloat() }
                            )
                        }
                    }
                }
            }

            item {
                Text("Last 7 days water", style = MaterialTheme.typography.titleMedium)

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        if (waterData.isEmpty()) {
                            Text("No data yet. Add water to see progress.")
                        } else {
                            BarChart(
                                labels = waterData.map { it.day.substring(5) }, // MM-dd
                                values = waterData.map { it.calories.toFloat() }
                            )
                        }
                    }
                }
            }

            item {
                Text("Last 7 days steps", style = MaterialTheme.typography.titleMedium)

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        if (stepsData.isEmpty()) {
                            Text("No data yet. Grant permission and walk to see progress.")
                        } else {
                            BarChart(
                                labels = stepsData.keys.map { it.substring(5) }, // MM-dd
                                values = stepsData.values.map { it.toFloat() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarChart(labels: List<String>, values: List<Float>) {
    val maxVal = max(values.maxOrNull() ?: 1f, 1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val barCount = values.size
        val spacing = size.width * 0.06f
        val usableWidth = size.width - spacing * (barCount + 1)
        val barWidth = usableWidth / barCount

        values.forEachIndexed { i, v ->
            val left = spacing + i * (barWidth + spacing)
            val barHeight = (v / maxVal) * size.height
            val top = size.height - barHeight

            drawRect(
                color = Color.Black.copy(alpha = 0.22f),
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }

    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEach { Text(it, style = MaterialTheme.typography.labelSmall) }
    }
}
