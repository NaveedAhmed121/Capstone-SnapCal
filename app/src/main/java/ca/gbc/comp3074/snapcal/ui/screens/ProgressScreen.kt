package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.ui.theme.*
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
    val context = LocalContext.current
    val caloriesData = progressVm.lastNDaysCaloriesFilled(7).collectAsState(initial = emptyList()).value
    val waterData    = progressVm.lastNDaysWaterFilled(7).collectAsState(initial = emptyList()).value
    val burnedData   = progressVm.lastNDaysBurnedFilled(7).collectAsState(initial = emptyList()).value
    
    val avgCalories by progressVm.getAverageCalories(7).collectAsState(initial = 0)
    val avgBurned by progressVm.getAverageBurned(7).collectAsState(initial = 0)

    val hasHCPerm   by healthConnectVm.hasHealthConnectPermissions
    var stepsData by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }

    LaunchedEffect(hasHCPerm) {
        if (hasHCPerm) {
            val today = LocalDate.now()
            val map = mutableMapOf<String, Long>()
            for (i in 0..6) {
                val date  = today.minusDays(i.toLong())
                val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                val end   = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                map[date.toString()] = healthConnectVm.readStepsByDate(context, start, end)
            }
            stepsData = map.toSortedMap()
        }
    }

    val totalConsumed = caloriesData.sumOf { it.calories }
    val totalBurned   = burnedData.sumOf { it.calories }
    val totalWater    = waterData.sumOf { it.calories }
    val netBalance    = totalBurned - totalConsumed

    val coachMsg = when {
        totalBurned > 0 && burnedData.count { it.calories > 0 } >= 4 -> "You are absolutely killing it this week. Keep this momentum going. 💪"
        totalBurned > 0 -> "Great consistency. One more push and this week is yours. 🔥"
        else -> "New week, fresh start. Log one activity and build the streak! 🌟"
    }

    Column(Modifier.fillMaxSize()) {
        // Pink gradient header
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text("📊 Your Progress", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(start = 8.dp)) {
                    SummaryChip("🍽️ Eaten", "${totalConsumed} kcal", Modifier.weight(1f))
                    SummaryChip("🔥 Burned", "${totalBurned} kcal", Modifier.weight(1f))
                    SummaryChip("💧 Water", "${totalWater} ml", Modifier.weight(1f))
                }
            }
        }

        LazyColumn(
            Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (netBalance >= 0) Color(0xFFD4EDDA) else Color(0xFFFDE2E1))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🔥 Weekly Calorie Outcome", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                            color = if (netBalance >= 0) Color(0xFF155724) else Color(0xFF721C24))
                        Text("Net Balance: ${if(netBalance>=0)"+$netBalance" else "$netBalance"} kcal",
                            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (netBalance >= 0) Color(0xFF155724) else Color(0xFF721C24))
                        Text("Avg Daily: In $avgCalories kcal / Out $avgBurned kcal", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            item {
                ProgressCard("📊 Calories In vs Out") {
                    if (caloriesData.isEmpty()) {
                        Text("No data yet. Add meals to see progress.", color = SubtleGray)
                    } else {
                        DualBarChart(
                            labels = caloriesData.map { it.day.substring(5) },
                            valuesA = caloriesData.map { it.calories.toFloat() },
                            valuesB = burnedData.map { it.calories.toFloat() },
                            colorA = Color(0xFFD1D5DB), colorB = BlueAccent
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem(Color(0xFFD1D5DB), "Consumed")
                            LegendItem(BlueAccent, "Burned")
                        }
                    }
                }
            }

            item {
                ProgressCard("🔥 Calories Burned (Active)") {
                    if (burnedData.isEmpty() || burnedData.all { it.calories == 0 }) {
                        Text("No workout data yet. Log an activity to see progress.", color = SubtleGray)
                    } else {
                        SingleBarChart(
                            labels = burnedData.map { it.day.substring(5) },
                            values = burnedData.map { it.calories.toFloat() },
                            barColor = Color(0xFFFFA726)
                        )
                    }
                }
            }

            item {
                ProgressCard("💧 Water Intake (ml)") {
                    if (waterData.isEmpty() || waterData.all { it.calories == 0 }) {
                        Text("No data yet. Add water entries to see chart.", color = SubtleGray)
                    } else {
                        SingleBarChart(waterData.map { it.day.substring(5) }, waterData.map { it.calories.toFloat() }, BlueAccent.copy(alpha = 0.7f))
                    }
                }
            }

            item {
                ProgressCard("👟 Steps History") {
                    when {
                        !hasHCPerm -> Text("Grant Health Connect permissions to see your steps chart.", color = SubtleGray)
                        stepsData.isEmpty() -> Text("No step data yet.", color = SubtleGray)
                        else -> SingleBarChart(stepsData.keys.map { it.substring(5) }, stepsData.values.map { it.toFloat() }, Color(0xFF66BB6A))
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F9))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("💬 Coach Message", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = PinkDark)
                        Text(coachMsg, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color(0xFF7B2F53))
                    }
                }
            }
            
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ProgressCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = PinkDark)
            content()
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.9f))) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = PinkDark)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PinkPrimary)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DualBarChart(labels: List<String>, valuesA: List<Float>, valuesB: List<Float>, colorA: Color, colorB: Color) {
    val maxVal = max((valuesA + valuesB).maxOrNull() ?: 1f, 1f)
    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
        val count = labels.size; if (count == 0) return@Canvas
        val gap = size.width * 0.04f
        val groupW = (size.width - gap * (count + 1)) / count
        val barW = groupW * 0.44f
        for (i in 0 until count) {
            val gx = gap + i * (groupW + gap)
            val hA = (valuesA.getOrElse(i){0f} / maxVal) * size.height * 0.85f
            val hB = (valuesB.getOrElse(i){0f} / maxVal) * size.height * 0.85f
            drawRect(colorA, Offset(gx, size.height - hA), Size(barW, hA))
            drawRect(colorB, Offset(gx + barW + 2f, size.height - hB), Size(barW, hB))
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEach { Text(it, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
private fun SingleBarChart(labels: List<String>, values: List<Float>, barColor: Color) {
    val maxVal = max(values.maxOrNull() ?: 1f, 1f)
    Canvas(Modifier.fillMaxWidth().height(130.dp)) {
        val count = labels.size; if (count == 0) return@Canvas
        val spacing = size.width * 0.05f
        val barWidth = (size.width - spacing * (count + 1)) / count
        values.forEachIndexed { i, v ->
            val left = spacing + i * (barWidth + spacing)
            val barH = (v / maxVal) * size.height * 0.85f
            drawRect(barColor, Offset(left, size.height - barH), Size(barWidth, barH))
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEach { Text(it, style = MaterialTheme.typography.labelSmall) }
    }
}
