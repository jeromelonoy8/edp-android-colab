package edu.liceo.fieldkit.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlin.math.abs

@Composable
fun rememberAccelerometer(): FloatArray {
    val context = LocalContext.current
    val sm = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accel = remember { sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    var values by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    // TODO 5a: register the listener while the screen is visible
    // TODO 5b: unregister it when it is not
    LifecycleResumeEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { values = it.copyOf() }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
        onPauseOrDispose { sm.unregisterListener(listener) }
    }
    return values
}

// TODO 6b: isLevel(x, y)
fun isLevel(x: Float, y: Float): Boolean {
    // Lying flat, x and y are near 0. Threshold of 0.5 m/s^2 is usually enough.
    return abs(x) < 0.5f && abs(y) < 0.5f
}