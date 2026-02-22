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
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HealthConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val app = getApplication<Application>()

    // Health Connect provider (Google Health Connect)
    private val providerPackageName = "com.google.android.apps.healthdata"

    val healthConnectPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    val normalPermissions: Set<String> = setOf(
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    // UI states
    val steps = mutableStateOf(0L)
    val hasHealthConnectPermissions = mutableStateOf(false)
    val hasNormalPermissions = mutableStateOf(false)

    fun initialCheck(context: Context = app) {
        refreshNormalPermissionsState(context)
        refreshHealthConnectPermissionsState(context)
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

    private fun clientOrNull(context: Context = app): HealthConnectClient? {
        return if (isAvailable(context)) HealthConnectClient.getOrCreate(context) else null
    }

    fun permissionRequestContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    fun openHealthConnectInPlayStore(context: Context = app) {
        val uri = Uri.parse("market://details?id=$providerPackageName")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
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

    fun refreshHealthConnectPermissionsState(context: Context = app) {
        val client = clientOrNull(context) ?: run {
            hasHealthConnectPermissions.value = false
            return
        }
        viewModelScope.launch {
            try {
                val granted = client.permissionController.getGrantedPermissions()
                hasHealthConnectPermissions.value = granted.containsAll(healthConnectPermissions)
            } catch (_: Exception) {
                hasHealthConnectPermissions.value = false
            }
        }
    }

    fun refreshSteps(context: Context = app) {
        val client = clientOrNull(context) ?: run {
            steps.value = 0L
            hasHealthConnectPermissions.value = false
            return
        }
        viewModelScope.launch {
            try {
                val granted = client.permissionController.getGrantedPermissions()
                val ok = granted.containsAll(healthConnectPermissions)
                hasHealthConnectPermissions.value = ok

                if (!ok) {
                    steps.value = 0L
                    return@launch
                }

                val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
                val now = Instant.now()
                steps.value = readStepsByDate(context, startOfDay, now)
            } catch (_: Exception) {
                steps.value = 0L
                hasHealthConnectPermissions.value = false
            }
        }
    }

    suspend fun readStepsByDate(context: Context, start: Instant, end: Instant): Long {
        val client = clientOrNull(context) ?: return 0L
        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response.records.sumOf { it.count }
    }
}
