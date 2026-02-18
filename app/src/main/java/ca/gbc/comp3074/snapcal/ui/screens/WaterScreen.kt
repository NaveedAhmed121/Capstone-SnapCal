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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    modifier: Modifier = Modifier,
    vm: WaterViewModel = viewModel()
) {
    val todayMl by vm.todayTotalMl.collectAsStateWithLifecycle()
    val entries by vm.entries.collectAsStateWithLifecycle()

    var customMlText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Water Tracker") },
            scrollBehavior = androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior(
                rememberTopAppBarState()
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Today")
                    Text("$todayMl ml")
                    Text("Tip: 2000 ml/day is a common goal")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.add(250) }, modifier = Modifier.weight(1f)) {
                    Text("+250")
                }
                Button(onClick = { vm.add(500) }, modifier = Modifier.weight(1f)) {
                    Text("+500")
                }
                Button(onClick = { vm.add(1000) }, modifier = Modifier.weight(1f)) {
                    Text("+1000")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customMlText,
                    onValueChange = { customMlText = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = { Text("Custom (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = {
                        val ml = customMlText.toIntOrNull() ?: return@OutlinedButton
                        vm.add(ml)
                        customMlText = ""
                    }
                ) {
                    Text("Add")
                }
            }

            Spacer(Modifier.height(6.dp))
            Text("History")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${entry.amountMl} ml")
                        Text(formatTime(entry.createdAt))
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
