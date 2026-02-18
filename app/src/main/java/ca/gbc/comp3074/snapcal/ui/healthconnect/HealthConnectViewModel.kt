package ca.gbc.comp3074.snapcal.ui.healthconnect

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val app = getApplication<Application>()

    private val clientOrNull: HealthConnectClient? =
        if (HealthConnectClient.getSdkStatus(app) == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(app)
        } else null

    // Permissions we need
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    // UI state
    val steps = mutableStateOf(0L)
    val isAvailable = mutableStateOf(clientOrNull != null)

    /** Call this from Dashboard on first load */
    fun initialLoad() {
        refreshSteps()
    }

    /** Re-read steps for today (returns 0 if not available or no permission) */
    fun refreshSteps() {
        val client = clientOrNull ?: run {
            steps.value = 0L
            isAvailable.value = false
            return
        }

        isAvailable.value = true

        viewModelScope.launch {
            try {
                val granted = client.permissionController.getGrantedPermissions()
                if (!granted.containsAll(permissions)) {
                    steps.value = 0L
                    return@launch
                }

                val now = Instant.now()
                val startOfDay = now.truncatedTo(ChronoUnit.DAYS)

                val response = client.readRecords(
                    ReadRecordsRequest(
                        recordType = StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                    )
                )

                steps.value = response.records.sumOf { it.count }
            } catch (_: Exception) {
                steps.value = 0L
            }
        }
    }

    /** Opens Health Connect in Play Store (user installs / opens app to grant permissions) */
    fun openHealthConnect(context: Context) {
        val uriString = "market://details?id=com.google.android.apps.healthdata"
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.android.vending")
                data = Uri.parse(uriString)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
