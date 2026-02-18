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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.viewmodel.ProgressViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    progressVm: ProgressViewModel
) {
    val data = progressVm.lastNDaysCalories(7).collectAsState().value

    val total = data.sumOf { it.calories }
    val avg = if (data.isNotEmpty()) total / data.size else 0
    val best = data.maxByOrNull { it.calories }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Last 7 days", style = MaterialTheme.typography.titleLarge)

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total",
                    value = if (data.isEmpty()) "—" else "$total kcal",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Average",
                    value = if (data.isEmpty()) "—" else "$avg kcal",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Best day",
                    value = if (best == null) "—" else "${best.day.substring(5)} • ${best.calories} kcal",
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Calories chart",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(10.dp))

                    if (data.isEmpty()) {
                        Text(
                            "No data yet. Add meals to see progress.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        CaloriesBarChart(
                            labels = data.map { it.day.substring(5) }, // MM-DD
                            values = data.map { it.calories }
                        )
                    }
                }
            }

            if (data.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Daily totals", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        data.forEach {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(it.day)
                                Text("${it.calories} kcal")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CaloriesBarChart(labels: List<String>, values: List<Int>) {
    val maxVal = max(values.maxOrNull() ?: 1, 1)

    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
    val bgLine = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)

    // Chart
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        // subtle baseline
        drawLine(
            color = bgLine,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f
        )

        val barCount = values.size
        val spacing = size.width * 0.06f
        val usableWidth = size.width - spacing * (barCount + 1)
        val barWidth = usableWidth / barCount

        values.forEachIndexed { i, v ->
            val left = spacing + i * (barWidth + spacing)
            val barHeight = (v.toFloat() / maxVal.toFloat()) * (size.height * 0.95f)
            val top = size.height - barHeight

            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(18f, 18f)
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    // Labels
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
