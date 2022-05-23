package com.example.appstraining.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.appstraining.stopwatch.databinding.ActivityMainBinding
import com.example.appstraining.stopwatch.databinding.ActivityMainBinding.inflate
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener { startTimer() }
        binding.btnStop.setOnClickListener { stopTimer() }
        binding.btnReset.setOnClickListener { resetTimer() }

        serviceIntent = Intent(applicationContext, TimerService :: class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            Log.d("Main", "time is recieved $time")
            binding.textView.text = getStringFromDouble(time)
            Log.d("Main", "${getStringFromDouble(time)}")
        }
    }

    private fun getStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()

        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60 % 60
        Log.d("Main", "Result Int: $resultInt ****   $hours:$minutes:$seconds")
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String {

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun startTimer() {
        if (!timerStarted) {
            serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
            startService(serviceIntent)
            timerStarted = true
        }
    }

    fun stopTimer() {
        if (timerStarted) {
            stopService(serviceIntent)
            timerStarted = false
        }
    }

    fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.textView.text = getStringFromDouble(time)
    }


}