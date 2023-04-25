package com.example.testdemo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var reminderText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var setReminderButton: Button
    private lateinit var countdownTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reminderText = findViewById(R.id.reminderText)
        timePicker = findViewById(R.id.reminderTimePicker)
        setReminderButton = findViewById(R.id.setReminderButton)
        countdownTextView = findViewById(R.id.countdownTextView)

        setReminderButton.setOnClickListener {
            setReminder()
        }
    }

    private fun setReminder() {
        val reminderTime = Calendar.getInstance()
        val currentTime = Calendar.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reminderTime.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            reminderTime.set(Calendar.MINUTE, timePicker.minute)
        } else {
            reminderTime.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
            reminderTime.set(Calendar.MINUTE, timePicker.currentMinute)
        }
        reminderTime.set(Calendar.SECOND, 0)

        if (reminderTime.before(currentTime)) {
            reminderTime.add(Calendar.DATE, 1)
        }

        setAlarm(reminderTime.timeInMillis)
        updateCountdownTextView(reminderTime.timeInMillis - currentTime.timeInMillis)
    }

    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        intent.putExtra("reminderText", reminderText.text.toString())
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun updateCountdownTextView(timeDifference: Long) {
        val hours = timeDifference / (60 * 60 * 1000)
        val minutes = (timeDifference / (60 * 1000)) % 60
        countdownTextView.text = String.format("倒计时：%02d:%02d", hours, minutes)
    }
}