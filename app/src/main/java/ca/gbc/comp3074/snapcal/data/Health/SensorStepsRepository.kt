package ca.gbc.comp3074.snapcal.data.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class SensorStepsRepository(private val context: Context) {

    fun interface StopHandle {
        fun stop()
    }

    fun start(
        onStepsToday: (Long) -> Unit,
        onError: (String) -> Unit
    ): StopHandle {

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter == null) {
            onError("Step Counter sensor not available on this device.")
            return StopHandle { /* no-op */ }
        }

        var initial: Float? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val totalSinceBoot = event.values.firstOrNull() ?: return
                if (initial == null) initial = totalSinceBoot
                val todaySteps = (totalSinceBoot - (initial ?: totalSinceBoot)).toLong().coerceAtLeast(0)
                onStepsToday(todaySteps)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        val ok = sensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        if (!ok) {
            onError("Failed to register step sensor listener.")
            return StopHandle { /* no-op */ }
        }

        return StopHandle {
            sensorManager.unregisterListener(listener)
        }
    }
}
