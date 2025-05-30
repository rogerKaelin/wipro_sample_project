package com.example.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.view.View
import android.widget.*
import com.example.demoapp.databinding.ActivityMainBinding
import timber.log.Timber               // Logging-Framework

/**
 * MainActivity demonstriert gängige UI-Controls
 * und navigiert bei Bedarf zur SensorActivity.
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding-Objekt für type-sicheren Zugriff aufs Layout
    private lateinit var vb: ActivityMainBinding

    /** Lifecycle-Einstiegspunkt */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout binden und anzeigen
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)

        // Timber für LogCat initialisieren (nur Debug-Builds)
        Timber.plant(Timber.DebugTree())

        initSpinner()   // Dropdown befüllen
        initListeners() // Event-Handler registrieren
    }

    /** Länder-Spinner initialisieren */
    private fun initSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.countries,                   // Daten aus strings.xml
            android.R.layout.simple_spinner_item // Default-Layout
        ).also {
            it.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        vb.spCountry.adapter = adapter
    }

    /** Alle Event-Listener registrieren */
    private fun initListeners() = with(vb) {

        // CheckBox: AGB akzeptiert / abgelehnt
        cbAgree.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(
                this@MainActivity,
                "AGB ${if (checked) "akzeptiert" else "abgelehnt"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Switch: WLAN aktiviert / deaktiviert
        swWifi.setOnCheckedChangeListener { _, state ->
            Timber.d("WLAN $state")
        }

        // RadioGroup: Option A oder B gewählt
        rgOptions.setOnCheckedChangeListener { _, id ->
            val opt = if (id == R.id.rbOptA) "A" else "B"
            Timber.i("Option $opt gewählt")
        }

        // Spinner: Land gewählt
        spCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p: AdapterView<*>, v: View?, pos: Int, id: Long
            ) {
                Timber.v("Land = %s", p.getItemAtPosition(pos))
            }
            override fun onNothingSelected(p: AdapterView<*>) {} // ignorieren
        }

        // Button: verstecktes Layout ein-/ausblenden
        btnShowMore.setOnClickListener {
            lyHidden.visibility =
                if (lyHidden.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // SeekBar: Lautstärke anzeigen
        sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                s: SeekBar, value: Int, fromUser: Boolean
            ) {
                tvVolume.text = "Lautstärke: ${value} %"
            }
            override fun onStartTrackingTouch(s: SeekBar) {}  // unused
            override fun onStopTrackingTouch(s: SeekBar) {}   // unused
        })

        // Button: zu SensorActivity navigieren
        btnNext.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, activitySensor::class.java)
            )
        }
    }
}
