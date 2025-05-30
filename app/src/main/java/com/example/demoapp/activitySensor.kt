package com.example.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.hardware.Sensor             // Sensor-Konstante (Typen)
import android.hardware.SensorEvent        // Rohdaten-Ereignis
import android.hardware.SensorEventListener// Interface für Callback
import android.hardware.SensorManager      // Liefert Sensor-Instanzen
import com.example.demoapp.databinding.ActivitySensorBinding
import timber.log.Timber                    // Logging-Framework

/**
 * Activity zeigt Live-Sensordaten.
 * Implementiert SensorEventListener, um Callbacks zu erhalten.
 */
class activitySensor : AppCompatActivity(), SensorEventListener {

    // ViewBinding-Objekt (type-safe Zugriff aufs Layout)
    private lateinit var vb: ActivitySensorBinding

    // Zentrale Sensorverwaltung
    private lateinit var sm: SensorManager

    /** Lifecycle: UI wird erzeugt */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivitySensorBinding.inflate(layoutInflater) // Layout binden
        setContentView(vb.root)

        // SensorManager-Instanz vom System holen
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Zurück-Button schließt die Activity
        vb.btnBack.setOnClickListener { finish() }
    }

    /** Lifecycle: Activity wird sichtbar → Sensoren abonnieren */
    override fun onResume() {
        super.onResume()

        // Liste der gewünschten Sensor-Typen
        val types = listOf(
            Sensor.TYPE_ACCELEROMETER,  // lineare Beschleunigung
            Sensor.TYPE_GYROSCOPE,      // Rotationsgeschwindigkeit
            Sensor.TYPE_LIGHT,          // Umgebungshelligkeit
            Sensor.TYPE_PROXIMITY       // Annäherung (z. B. Ohr beim Telefonat)
        )

        // Jeden Sensor registrieren (falls vorhanden)
        types.forEach { type ->
            sm.getDefaultSensor(type)?.also { sensor ->
                // this = Activity selbst fungiert als Listener
                sm.registerListener(
                    /* listener = */ this,
                    /* sensor   = */ sensor,
                    /* rate     = */ SensorManager.SENSOR_DELAY_UI  // ~16 ms / 60 Hz
                )
            } ?: Timber.w("Sensor %d nicht vorhanden", type)
        }
    }

    /** Lifecycle: Activity verlassen → Listener abmelden  */
    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this)     // Energie sparen, Memory-Leak vermeiden
    }

    /** Callback: neue Sensordaten eingetroffen */
    override fun onSensorChanged(e: SensorEvent) = with(vb) {
        when (e.sensor.type) {

            // Beschleunigung in m/s² auf X, Y, Z
            Sensor.TYPE_ACCELEROMETER ->
                tvAccel.text = "Accel: %.2f, %.2f, %.2f m/s²"
                    .format(e.values[0], e.values[1], e.values[2])

            // Drehgeschwindigkeit in rad/s
            Sensor.TYPE_GYROSCOPE ->
                tvGyro.text = "Gyro: %.2f, %.2f, %.2f rad/s"
                    .format(e.values[0], e.values[1], e.values[2])

            // Lichtstärke in Lux
            Sensor.TYPE_LIGHT ->
                tvLight.text = "Light: %.1f lx".format(e.values[0])

            // Abstand in Zentimetern (0 cm = sehr nah)
            Sensor.TYPE_PROXIMITY ->
                tvProx.text = "Proximity: %.1f cm".format(e.values[0])
        }
    }

    /** Callback: Genauigkeit des Sensors hat sich geändert (hier ignoriert) */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}