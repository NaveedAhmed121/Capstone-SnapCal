package ca.gbc.comp3074.snapcal.ui.healthconnect

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class HealthConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val app = getApplication<Application>()
    private val providerPackageName = "com.google.android.apps.healthdata"

    val healthConnectPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )
    val normalPermissions: Set<String> = setOf(Manifest.permission.ACTIVITY_RECOGNITION)

    val steps                    = mutableStateOf(0L)
    val hasHealthConnectPermissions = mutableStateOf(false)
    val hasNormalPermissions        = mutableStateOf(false)

    // Called on app launch
    fun initialCheck(context: Context = app) {
        refreshNormalPermissionsState(context)
        viewModelScope.launch {
            refreshHealthConnectPermissionsStateAsync(context)
            if (hasHealthConnectPermissions.value) {
                loadSteps(context)
            }
        }
    }

    fun sdkStatus(context: Context = app): Int =
        HealthConnectClient.getSdkStatus(context, providerPackageName)

    fun isAvailable(context: Context = app): Boolean =
        sdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    fun needsInstallOrUpdate(context: Context = app): Boolean {
        val status = sdkStatus(context)
        return status == HealthConnectClient.SDK_UNAVAILABLE ||
               status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
    }

    private fun clientOrNull(context: Context = app): HealthConnectClient? =
        if (isAvailable(context)) HealthConnectClient.getOrCreate(context) else null

    fun permissionRequestContract(): ActivityResultContract<Set<String>, Set<String>> =
        PermissionController.createRequestPermissionResultContract()

    fun openHealthConnectInPlayStore(context: Context = app) {
        val intent = Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$providerPackageName")).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun refreshNormalPermissionsState(context: Context = app) {
        hasNormalPermissions.value = normalPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    private suspend fun refreshHealthConnectPermissionsStateAsync(context: Context = app) {
        val client = clientOrNull(context) ?: run {
            hasHealthConnectPermissions.value = false; return
        }
        try {
            val granted = client.permissionController.getGrantedPermissions()
            hasHealthConnectPermissions.value = granted.containsAll(healthConnectPermissions)
        } catch (_: Exception) {
            hasHealthConnectPermissions.value = false
        }
    }

    fun refreshHealthConnectPermissionsState(context: Context = app) {
        viewModelScope.launch { refreshHealthConnectPermissionsStateAsync(context) }
    }

    private suspend fun loadSteps(context: Context = app) {
        val client = clientOrNull(context) ?: return
        try {
            val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfNow = Instant.now()
            
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfNow)
                )
            )
            steps.value = response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            steps.value = 0L
        }
    }

    fun refreshSteps(context: Context = app) {
        viewModelScope.launch {
            val client = clientOrNull(context) ?: run {
                steps.value = 0L; hasHealthConnectPermissions.value = false; return@launch
            }
            try {
                val granted = client.permissionController.getGrantedPermissions()
                val ok = granted.containsAll(healthConnectPermissions)
                hasHealthConnectPermissions.value = ok
                if (ok) {
                    loadSteps(context)
                }
            } catch (_: Exception) {
                steps.value = 0L; hasHealthConnectPermissions.value = false
            }
        }
    }

    suspend fun readStepsByDate(context: Context, startTime: Instant, endTime: Instant): Long {
        val client = clientOrNull(context) ?: return 0L
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun addMockSteps(context: Context = app) {
        viewModelScope.launch {
            val client = clientOrNull(context) ?: return@launch
            try {
                val now = Instant.now()
                val record = StepsRecord(
                    count = 500L,
                    startTime = now.minusSeconds(60),
                    endTime = now,
                    startZoneOffset = ZonedDateTime.now().offset,
                    endZoneOffset = ZonedDateTime.now().offset,
                    metadata = Metadata()
                )
                client.insertRecords(listOf(record))
                // Small delay to ensure Health Connect processes the insertion
                kotlinx.coroutines.delay(1000)
                loadSteps(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
