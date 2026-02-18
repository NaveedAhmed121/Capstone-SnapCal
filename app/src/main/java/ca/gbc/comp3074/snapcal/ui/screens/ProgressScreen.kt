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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Last 7 days calories", style = MaterialTheme.typography.titleMedium)

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    if (data.isEmpty()) {
                        Text("No data yet. Add meals to see progress.")
                    } else {
                        CaloriesBarChart(
                            labels = data.map { it.day.substring(5) }, // show MM-DD
                            values = data.map { it.calories }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            if (data.isNotEmpty()) {
                Text("Daily totals:")
                data.forEach {
                    Text("${it.day}: ${it.calories} kcal")
                }
            }
        }
    }
}

@Composable
private fun CaloriesBarChart(labels: List<String>, values: List<Int>) {
    val maxVal = max(values.maxOrNull() ?: 1, 1)

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
            val barHeight = (v.toFloat() / maxVal.toFloat()) * size.height
            val top = size.height - barHeight
            drawRect(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.25f),
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
