package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.data.model.WorkoutEntry
import ca.gbc.comp3074.snapcal.ui.theme.*
import ca.gbc.comp3074.snapcal.ui.workout.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────
//  Activity preset catalogue
// ─────────────────────────────────────────────────────────────────

data class ActivityPreset(
    val name: String,
    val icon: String,
    val category: String,
    val calPerMin: Double,     // kcal burned per minute at moderate intensity (70 kg person)
    val description: String
)

private val ACTIVITY_CATEGORIES = listOf("All", "Cardio", "Strength", "Sports", "Flexibility", "Daily")

private val ACTIVITY_PRESETS = listOf(
    // CARDIO
    ActivityPreset("Running",          "🏃", "Cardio",    10.0, "Outdoor / treadmill, moderate pace ~9 km/h"),
    ActivityPreset("Fast Running",     "💨", "Cardio",    14.0, "High-intensity ~13 km/h or sprint intervals"),
    ActivityPreset("Walking",          "🚶", "Cardio",     4.0, "Brisk walk, ~5–6 km/h"),
    ActivityPreset("Hiking",           "🥾", "Cardio",     6.5, "Hilly trail with pack"),
    ActivityPreset("Cycling",          "🚴", "Cardio",     9.0, "Moderate pace ~20 km/h"),
    ActivityPreset("Cycling (fast)",   "🚵", "Cardio",    13.0, "High-intensity or uphill, ~28 km/h"),
    ActivityPreset("Swimming",         "🏊", "Cardio",    10.0, "Freestyle laps, moderate effort"),
    ActivityPreset("Jump Rope",        "🪢", "Cardio",    12.0, "Continuous skipping, moderate speed"),
    ActivityPreset("Rowing (machine)", "🚣", "Cardio",     9.5, "Moderate effort on erg"),
    ActivityPreset("Elliptical",       "⚙️","Cardio",     7.5, "Moderate resistance"),
    ActivityPreset("Stair Climbing",   "🪜", "Cardio",     9.0, "Stair machine or stairs"),
    ActivityPreset("HIIT",             "⚡", "Cardio",    13.5, "High-intensity interval training"),
    ActivityPreset("Kickboxing",       "🥊", "Cardio",    10.0, "Cardio kickboxing class"),

    // STRENGTH
    ActivityPreset("Weight Training",  "🏋️","Strength",   6.0, "General lifting, rest between sets"),
    ActivityPreset("Powerlifting",     "💪", "Strength",   7.0, "Heavy compound lifts"),
    ActivityPreset("CrossFit",         "🔥", "Strength",  12.0, "WOD — high intensity mixed training"),
    ActivityPreset("Bodyweight HIIT",  "🤸", "Strength",   9.0, "Push-ups, squats, burpees circuits"),
    ActivityPreset("Pull-ups / Dips",  "🙌", "Strength",   6.5, "Upper body calisthenics"),
    ActivityPreset("Core / Abs",       "🫀", "Strength",   4.5, "Planks, crunches, sit-ups"),

    // SPORTS
    ActivityPreset("Football / Soccer","⚽", "Sports",    10.0, "Recreational match, active play"),
    ActivityPreset("Basketball",       "🏀", "Sports",    10.5, "Recreational game"),
    ActivityPreset("Tennis",           "🎾", "Sports",     8.0, "Singles match"),
    ActivityPreset("Badminton",        "🏸", "Sports",     6.5, "Recreational singles"),
    ActivityPreset("Cricket",          "🏏", "Sports",     5.0, "Batting and fielding"),
    ActivityPreset("Volleyball",       "🏐", "Sports",     4.5, "Recreational, non-competitive"),
    ActivityPreset("Swimming (sport)", "🏊", "Sports",    11.0, "Competitive / fast laps"),
    ActivityPreset("Boxing",           "🥊", "Sports",    12.0, "Sparring or heavy bag work"),
    ActivityPreset("Martial Arts",     "🥋", "Sports",     9.5, "Karate, taekwondo, BJJ training"),
    ActivityPreset("Dancing",          "💃", "Sports",     6.0, "Zumba, salsa, hip-hop class"),
    ActivityPreset("Rock Climbing",    "🧗", "Sports",     9.0, "Indoor bouldering or sport climb"),
    ActivityPreset("Skateboarding",    "🛹", "Sports",     5.5, "Active skating, tricks"),
    ActivityPreset("Kayaking",         "🛶", "Sports",     6.0, "Moderate paddling"),

    // FLEXIBILITY / MIND-BODY
    ActivityPreset("Yoga",             "🧘", "Flexibility", 3.0, "Hatha / flow yoga"),
    ActivityPreset("Hot Yoga",         "🌡️","Flexibility", 4.5, "Bikram / heated class"),
    ActivityPreset("Pilates",          "🤸", "Flexibility", 3.5, "Mat or reformer Pilates"),
    ActivityPreset("Stretching",       "🦵", "Flexibility", 2.0, "Static / dynamic stretching"),
    ActivityPreset("Tai Chi",          "☯️", "Flexibility", 2.5, "Slow flowing movements"),

    // DAILY ACTIVITIES
    ActivityPreset("Cleaning / Chores","🧹", "Daily",     3.0, "Vacuuming, mopping, scrubbing"),
    ActivityPreset("Gardening",        "🌱", "Daily",     4.0, "Digging, planting, mowing"),
    ActivityPreset("Carrying Groceries","🛍️","Daily",     3.5, "Walking with heavy bags"),
    ActivityPreset("Playing with Kids", "👶","Daily",     4.0, "Active running / chasing"),
    ActivityPreset("Dog Walking",      "🐕", "Daily",     3.5, "Brisk walk with dog")
)

// ─────────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(vm: WorkoutViewModel, onBack: () -> Unit = {}) {
    val allEntries  by vm.allEntries.collectAsState(initial = emptyList())
    val todayBurned by vm.todayBurned.collectAsState(initial = 0)
    val upcoming    by vm.observeUpcoming(System.currentTimeMillis()).collectAsState(initial = emptyList())
    val activities  = allEntries.filter { !it.isScheduled }

    var tab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        // ── Gradient header ──────────────────────────────────────
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Column {
                        Text("🏋️ Workout & Activity",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Track your workouts and calories burned",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.85f))
                    }
                }
                // Summary chips
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryPill("🔥 Today", "$todayBurned kcal burned")
                    SummaryPill("📋 Total", "${activities.size} logged")
                }
            }
        }

        // ── Tabs ─────────────────────────────────────────────────
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 },
                text = { Text("📋 Log Activity") })
            Tab(selected = tab == 1, onClick = { tab = 1 },
                text = { Text("🗓️ Calendar") })
            Tab(selected = tab == 2, onClick = { tab = 2 },
                text = { Text("📊 History") })
        }

        when (tab) {
            0 -> LogActivityTab(vm)
            1 -> CalendarTab(vm, upcoming)
            2 -> HistoryTab(activities, vm)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  Tab 0 — Log Activity (preset picker + manual entry)
// ─────────────────────────────────────────────────────────────────

@Composable
private fun LogActivityTab(vm: WorkoutViewModel) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPreset   by remember { mutableStateOf<ActivityPreset?>(null) }
    var durationMin      by remember { mutableStateOf("30") }
    var weightKg         by remember { mutableStateOf("70") }
    var actDate          by remember { mutableStateOf(todayIso()) }
    var manualName       by remember { mutableStateOf("") }
    var manualCal        by remember { mutableStateOf("") }
    var showManual       by remember { mutableStateOf(false) }
    var savedMsg         by remember { mutableStateOf("") }

    // Calculated kcal based on preset + duration + weight
    val estimatedKcal = remember(selectedPreset, durationMin, weightKg) {
        val preset = selectedPreset ?: return@remember 0
        val mins   = durationMin.toDoubleOrNull() ?: 30.0
        val kg     = weightKg.toDoubleOrNull() ?: 70.0
        // MET-based scaling: heavier person burns more
        val factor = kg / 70.0
        (preset.calPerMin * mins * factor).toInt()
    }

    val filteredPresets = remember(selectedCategory) {
        if (selectedCategory == "All") ACTIVITY_PRESETS
        else ACTIVITY_PRESETS.filter { it.category == selectedCategory }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Success message ──
        if (savedMsg.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))) {
                    Text(savedMsg, Modifier.padding(14.dp), color = Color(0xFF155724),
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // ── Duration & Weight ──
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⏱️ Session Settings", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold, color = PinkDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = durationMin,
                            onValueChange = { durationMin = it.filter(Char::isDigit).take(3) },
                            label = { Text("Duration (min)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = weightKg,
                            onValueChange = { weightKg = it.filter(Char::isDigit).take(3) },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors()
                        )
                        OutlinedTextField(
                            value = actDate,
                            onValueChange = { actDate = it },
                            label = { Text("Date") },
                            modifier = Modifier.weight(1.2f), shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors()
                        )
                    }
                    // Quick duration chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("15","20","30","45","60","90").forEach { d ->
                            FilterChip(
                                selected = durationMin == d,
                                onClick  = { durationMin = d },
                                label    = { Text("${d}m") }
                            )
                        }
                    }
                }
            }
        }

        // ── Category filter ──
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🏷️ Category", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold, color = PinkDark)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ACTIVITY_CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick  = { selectedCategory = cat; selectedPreset = null },
                            label    = { Text(cat) }
                        )
                    }
                }
            }
        }

        // ── Activity grid ──
        item {
            Text("Choose Activity", style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold, color = PinkDark)
        }

        // Render in pairs (2-column grid feel without LazyVerticalGrid nesting issue)
        itemsIndexed(filteredPresets.chunked(2)) { _, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    val kc = if (preset == selectedPreset) estimatedKcal
                             else {
                                 val mins = durationMin.toDoubleOrNull() ?: 30.0
                                 val kg   = weightKg.toDoubleOrNull()   ?: 70.0
                                 (preset.calPerMin * mins * (kg / 70.0)).toInt()
                             }
                    ActivityCard(
                        preset     = preset,
                        kcal       = kc,
                        isSelected = isSelected,
                        modifier   = Modifier.weight(1f),
                        onClick    = { selectedPreset = if (isSelected) null else preset }
                    )
                }
                // Padding if odd number in row
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // ── Selected preset detail + Log button ──
        selectedPreset?.let { preset ->
            item {
                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(preset.icon, fontSize = 36.sp)
                            Column(Modifier.weight(1f)) {
                                Text(preset.name, fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium)
                                Text(preset.description, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.75f))
                            }
                        }
                        // Kcal estimate box
                        Box(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PinkPrimary)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Estimated Burn", style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(0.85f))
                                Text("~$estimatedKcal kcal",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("$durationMin min  •  ${weightKg} kg body weight",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(0.8f))
                            }
                        }
                        // Per-minute breakdown
                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                            MiniStat("⚡ Rate",    "${preset.calPerMin.toInt()} kcal/min")
                            MiniStat("⏱️ Duration", "$durationMin min")
                            MiniStat("🔥 Total",   "$estimatedKcal kcal")
                        }
                        Button(
                            onClick = {
                                val millis = parseDate(actDate)
                                if (millis != null) {
                                    vm.addActivity(preset.name, estimatedKcal, millis)
                                    savedMsg = "✅ ${preset.name} logged — $estimatedKcal kcal burned!"
                                    selectedPreset = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Log ${preset.name} — $estimatedKcal kcal",
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // ── Manual entry toggle ──
        item {
            OutlinedButton(
                onClick = { showManual = !showManual; savedMsg = "" },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Icon(if (showManual) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                Spacer(Modifier.width(8.dp))
                Text("Enter activity manually")
            }
        }

        item {
            AnimatedVisibility(visible = showManual, enter = expandVertically(), exit = shrinkVertically()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("✏️ Manual Entry", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold, color = PinkDark)
                        OutlinedTextField(
                            value = manualName, onValueChange = { manualName = it },
                            label = { Text("Activity name") },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                            colors = textFieldColors()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = manualCal, onValueChange = { manualCal = it.filter(Char::isDigit) },
                                label = { Text("Calories burned") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors()
                            )
                            OutlinedTextField(
                                value = actDate, onValueChange = { actDate = it },
                                label = { Text("Date") },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors()
                            )
                        }
                        Button(
                            onClick = {
                                val cal = manualCal.toIntOrNull() ?: 0
                                val millis = parseDate(actDate)
                                if (manualName.isNotBlank() && cal > 0 && millis != null) {
                                    vm.addActivity(manualName, cal, millis)
                                    savedMsg = "✅ ${manualName} logged — $cal kcal!"
                                    manualName = ""; manualCal = ""
                                    showManual = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                            enabled  = manualName.isNotBlank() && manualCal.isNotBlank()
                        ) { Text("+ Log Activity") }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  Tab 1 — Workout Calendar
// ─────────────────────────────────────────────────────────────────

@Composable
private fun CalendarTab(vm: WorkoutViewModel, upcoming: List<WorkoutEntry>) {
    var wkName by remember { mutableStateOf("") }
    var wkDate by remember { mutableStateOf(tomorrowIso()) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🗓️ Schedule Workout", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold, color = PinkDark)
                    // Quick workout name chips
                    Text("Quick select:", style = MaterialTheme.typography.labelSmall, color = SubtleGray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("🏃 Run","🏋️ Gym","🚴 Cycle","🏊 Swim","🧘 Yoga","⚽ Football","🥊 Boxing","💪 HIIT")) { w ->
                            SuggestionChip(onClick = { wkName = w }, label = { Text(w) })
                        }
                    }
                    OutlinedTextField(
                        value = wkName, onValueChange = { wkName = it },
                        label = { Text("Workout name") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors()
                    )
                    OutlinedTextField(
                        value = wkDate, onValueChange = { wkDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors()
                    )
                    Button(
                        onClick = {
                            val millis = parseDate(wkDate)
                            if (wkName.isNotBlank() && millis != null) {
                                vm.scheduleWorkout(wkName, millis)
                                wkName = ""; wkDate = tomorrowIso()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                        enabled  = wkName.isNotBlank()
                    ) { Text("📅 Add to Calendar") }
                }
            }
        }

        item {
            Text("Upcoming Workouts (${upcoming.size})",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }

        if (upcoming.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📅", fontSize = 40.sp)
                        Text("No upcoming workouts", fontWeight = FontWeight.SemiBold)
                        Text("Schedule your next session above",
                            style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                    }
                }
            }
        } else {
            items(upcoming, key = { it.id }) { entry ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(44.dp).clip(CircleShape).background(PinkPrimary.copy(0.15f)),
                                contentAlignment = Alignment.Center
                            ) { Text("📅", fontSize = 20.sp) }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(entry.name, fontWeight = FontWeight.SemiBold)
                                Text(fmtDate(entry.dateMillis),
                                    style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                            }
                        }
                        IconButton(onClick = { vm.delete(entry) }) {
                            Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  Tab 2 — Activity History
// ─────────────────────────────────────────────────────────────────

@Composable
private fun HistoryTab(activities: List<WorkoutEntry>, vm: WorkoutViewModel) {
    val totalBurned = activities.sumOf { it.caloriesBurned }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (activities.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MiniStat("📋 Sessions", "${activities.size}")
                        MiniStat("🔥 Total burned", "$totalBurned kcal")
                        MiniStat("⚡ Avg/session", "${if (activities.isEmpty()) 0 else totalBurned / activities.size} kcal")
                    }
                }
            }
        }

        item {
            Text("All Activities", style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
        }

        if (activities.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🏃", fontSize = 40.sp)
                        Text("No activities logged yet", fontWeight = FontWeight.SemiBold)
                        Text("Use the Log Activity tab to get started",
                            style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                    }
                }
            }
        } else {
            items(activities, key = { it.id }) { entry ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)) {
                            // Icon badge
                            val icon = ACTIVITY_PRESETS.find { p ->
                                p.name.equals(entry.name, ignoreCase = true)
                            }?.icon ?: "🏃"
                            Box(
                                Modifier.size(44.dp).clip(CircleShape)
                                    .background(PinkPrimary.copy(0.12f)),
                                contentAlignment = Alignment.Center
                            ) { Text(icon, fontSize = 20.sp) }
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(entry.name, fontWeight = FontWeight.SemiBold)
                                Text(fmtDate(entry.dateMillis),
                                    style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${entry.caloriesBurned} kcal",
                                fontWeight = FontWeight.Bold, color = PinkPrimary)
                            TextButton(
                                onClick = { vm.delete(entry) },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text("Remove", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  Small composables
// ─────────────────────────────────────────────────────────────────

@Composable
private fun ActivityCard(
    preset: ActivityPreset,
    kcal: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PinkPrimary else Color.Transparent
    val bgColor     = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.surface

    Card(
        modifier  = modifier.clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(14.dp)),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(preset.icon, fontSize = 28.sp, textAlign = TextAlign.Center)
            Text(preset.name,
                style       = MaterialTheme.typography.labelMedium,
                fontWeight  = FontWeight.SemiBold,
                textAlign   = TextAlign.Center,
                maxLines    = 2,
                color       = if (isSelected) PinkDark else MaterialTheme.colorScheme.onSurface)
            Text("~$kcal kcal",
                style      = MaterialTheme.typography.labelSmall,
                color      = if (isSelected) PinkPrimary else SubtleGray,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SummaryPill(label: String, value: String) {
    Card(shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.9f))) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = PinkDark)
            Text(value, fontWeight = FontWeight.Bold, color = PinkPrimary,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubtleGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor      = Color.Black,
    unfocusedTextColor    = Color.Black,
    focusedContainerColor   = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor    = PinkPrimary,
    unfocusedBorderColor  = Color(0xFFE0E0E0)
)

// ─────────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────────

private fun todayIso()    = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
private fun tomorrowIso() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    .format(Date(System.currentTimeMillis() + 86_400_000L))
private fun parseDate(s: String): Long? = try {
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(s)?.time
} catch (_: Exception) { null }
private fun fmtDate(ms: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(ms))
