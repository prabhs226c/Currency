package com.prabh.currency


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private var mSense: Sensor? = null

    private var mShakeCount: Int = 0

    private var gForce: Float = 0f
    private var gForceOld: Float = 0f
    private var mAccel: Float = 0f

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            gForceOld = gForce
            gForce = sqrt(x * gX + y * gY + z * gZ)

            val delta = gForce - gForceOld

            mAccel = mAccel * 0.9F + delta

            if (mAccel > 11) {
                if (mShakeCount > 4) {

                    mShakeCount = 0
                    submitView()
                    val v =
                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
// Vibrate for 500 milliseconds
// Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(
                            VibrationEffect.createOneShot(
                                200,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        //deprecated in API 26
                        v.vibrate(500)
                    }
                } else {
                    mShakeCount++
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentContext = this
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSense = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        findViewById<ImageView>(R.id.convertCurrency).apply {
            setOnClickListener {
                val currencyFrom =
                    currentContext.findViewById<Spinner>(R.id.countryFrom).selectedItem.toString()
                val currencyTo =
                    currentContext.findViewById<Spinner>(R.id.countryTo).selectedItem.toString()
                submitView()
            }
        }
    }

    fun submitView() {
        val currencyFrom = this.findViewById<Spinner>(R.id.countryFrom).selectedItem.toString()
        val currencyTo = this.findViewById<Spinner>(R.id.countryTo).selectedItem.toString()
        val amount = this.findViewById<EditText>(R.id.moneyValue).text

        when {
            currencyFrom === currencyTo -> {
                val errToast =
                    Toast.makeText(this, "Both currencies cannot be same", Toast.LENGTH_SHORT)
                errToast.show()
                return
            }
            amount.isEmpty() -> {
                val errToast = Toast.makeText(this, "Please  Enter a value", Toast.LENGTH_SHORT)
                errToast.show()
                return
            }
            amount.toString().toFloat() === 0f -> {
                val errToast = Toast.makeText(this, "Value cannot be zero", Toast.LENGTH_SHORT)
                errToast.show()
                return
            }
            else -> {
                val intent = Intent(this, ResultActivity::class.java)
                intent.apply {
                    putExtra("fromCurrency", currencyFrom)
                    putExtra("toCurrency", currencyTo)
                    putExtra("amount", amount.toString().toFloat())
                }
                var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.coin)
                mediaPlayer?.start()
//                mp.start()
                startActivity(intent)
            }

        }

    }

    override fun onResume() {
        super.onResume()
//        Register the sensor on resume of the activity
        mSensorManager.registerListener(this, mSense, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
//        unregister the sensor onPause else it will be active even if the activity is closed
        mSensorManager.unregisterListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.linkSbout -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            R.id.linkExit -> {
                exitApp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    fun exitApp() {
        finishAffinity()
    }
}
