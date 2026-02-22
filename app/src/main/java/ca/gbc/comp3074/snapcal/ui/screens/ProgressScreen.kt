package ca.gbc.comp3074.snapcal.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val hasPermission by healthConnectVm.hasHealthConnectPermissions

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val today = LocalDate.now()
            val newStepsData = mutableMapOf<String, Long>()
            for (i in 0..6) {
                val date = today.minusDays(i.toLong())
                val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                val steps = healthConnectVm.readStepsByDate(context, start, end)
                newStepsData[date.toString()] = steps
            }
            stepsData = newStepsData
        }
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
                        if (!hasPermission) {
                            Text("Please grant Health Connect permissions to see your steps progress.")
                        } else if (stepsData.isEmpty()) {
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
    val labelColor = MaterialTheme.colorScheme.onSurface
    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Increased height for labels
    ) {
        val barCount = values.size
        if (barCount == 0) return@Canvas

        val spacing = size.width * 0.06f
        val usableWidth = size.width - spacing * (barCount + 1)
        val barWidth = usableWidth / barCount
        val labelTextSize = 12.sp.toPx()

        values.forEachIndexed { i, v ->
            val left = spacing + i * (barWidth + spacing)
            val barHeight = (v / maxVal) * (size.height - labelTextSize * 2) // Adjust height for labels
            val top = (size.height - labelTextSize * 2) - barHeight

            // Draw Bar
            drawRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Draw Label
            if (i < labels.size) {
                val label = labels[i]
                val paint = Paint().apply {
                    color = labelColor.toArgb()
                    textAlign = Paint.Align.CENTER
                    textSize = labelTextSize
                }
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    left + barWidth / 2,
                    size.height - 5f, // Position label at the bottom
                    paint
                )
            }
        }
    }
}
