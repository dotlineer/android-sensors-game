package com.example.a2048c2

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameView: GameView
    private var sensorManager: SensorManager? = null
    private var sensorAcc: Sensor? = null
    private var sensorLight: Sensor? = null
    private var lastTilt: Tilt? = null
    private var currentColorScheme: ColorScheme = ColorScheme.LIGHT

    private var startTime: Long = 0

    private var timerHandler: Handler  = Handler();
    private var timerRunnable: Runnable? = null

    private lateinit var tvTimer: TextView


    inner class TimeRunnable : Runnable {
        override fun run() {
            var millis: Long = System.currentTimeMillis() - startTime;
            var seconds: Long = (millis / 1000);
            var minutes: Long = seconds / 60;
            seconds = seconds % 60;


            tvTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    }


    var score = 0
        set(score) {
            field = score
            (findViewById<TextView>(R.id.tvScore)).text = "$score"
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this
        gameView = findViewById<GameView>(R.id.gameView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAcc = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, sensorAcc, 20000000)

        sensorLight = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        tvTimer = findViewById<TextView>(R.id.tvTimer)
        timerRunnable = TimeRunnable()
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }


    fun restart(view: View) = gameView.startGame()





    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]

            Log.d("VAL: x", x.toString())
            Log.d("VAL: y", y.toString())


            if (Math.abs(x) <= CENTERED_THESHOLD && Math.abs(y) <= CENTERED_THESHOLD) {
                lastTilt = Tilt.CENTERED
                Log.d("SENSOR", "Centered")
            }

            if (Math.abs(x) > Math.abs(y)) {
                if (x < RIGHT_TILT_THRESHOLD && lastTilt == Tilt.CENTERED) {
                    Log.d("SENSOR", "Tilted right")
                    lastTilt = Tilt.RIGHT
                    gameView.swipeRight()
                }
                if (x > LEFT_TILT_THRESHOLD && lastTilt == Tilt.CENTERED) {
                    Log.d("SENSOR", "Tilted left")
                    lastTilt = Tilt.LEFT
                    gameView.swipeLeft()
                }
            } else {
                if (y < UP_TILT_THRESHOLD && lastTilt == Tilt.CENTERED) {
                    Log.d("SENSOR", "Tilted up")
                    lastTilt = Tilt.UP
                    gameView.swipeUp()
                }
                if (y > DOWN_TILT_THRESHOLD && lastTilt == Tilt.CENTERED) {
                    Log.d("SENSOR", "Tilted down")
                    lastTilt = Tilt.DOWN
                    gameView.swipeDown()
                }
            }
        }

        else if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val value: Float = event.values[0]
            Log.d("LIGHT_SENSOR", value.toString())

            if (value < LIGHT_INTENSITY_DARK_SCHEME_THRESHOLD && currentColorScheme != ColorScheme.DARK) {
                changeColorScheme(ColorScheme.DARK)
                currentColorScheme = ColorScheme.DARK
            }
            else if (currentColorScheme != ColorScheme.LIGHT) {
                changeColorScheme(ColorScheme.LIGHT)
                currentColorScheme = ColorScheme.LIGHT
            }
        }



    }

    private fun changeColorScheme(scheme: ColorScheme) {
        if (scheme == ColorScheme.DARK) {
            gameView.setBackgroundColor(Color.parseColor(Utils.DARK_SCHEME_COLOR))
            val cl = findViewById<ConstraintLayout>(R.id.constraintLayout) as ConstraintLayout
            cl.setBackgroundColor(Color.parseColor(Utils.DARK_SCHEME_COLOR))
        }
        else if (scheme == ColorScheme.LIGHT) {
            gameView.setBackgroundColor(Color.parseColor(Utils.LIGHT_SCHEME_COLOR))
            val cl = findViewById<ConstraintLayout>(R.id.constraintLayout) as ConstraintLayout
            cl.setBackgroundColor(Color.parseColor(Utils.LIGHT_SCHEME_COLOR))
        }
    }


    override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }



    companion object {
        var mainActivity: MainActivity? = null
            private set

        val CENTERED_THESHOLD = 4
        val RIGHT_TILT_THRESHOLD = -5
        val LEFT_TILT_THRESHOLD = -RIGHT_TILT_THRESHOLD
        val UP_TILT_THRESHOLD = -5
        val DOWN_TILT_THRESHOLD = 5

        val LIGHT_INTENSITY_DARK_SCHEME_THRESHOLD = 5

    }

}


internal enum class Tilt {
    LEFT, RIGHT, DOWN, UP, CENTERED
}

internal enum class ColorScheme {
    DARK, LIGHT
}

