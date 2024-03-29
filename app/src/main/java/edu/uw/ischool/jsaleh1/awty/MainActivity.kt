package edu.uw.ischool.jsaleh1.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.Locale

const val ALARM_ACTION = "edu.uw.ischool.jsaleh1.ALARM"
class MainActivity : AppCompatActivity() {
        lateinit var messageEdit : EditText
        lateinit var phoneEdit : EditText
        lateinit var timeEdit : EditText
        lateinit var startBtn : Button
        var awtyOn = false
        var receiver : BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageEdit = findViewById(R.id.messageEdit)
        phoneEdit = findViewById(R.id.numberEdit)
        timeEdit = findViewById(R.id.timeEdit)
        startBtn = findViewById(R.id.startBtn)

        startBtn.setOnClickListener {
            if(!awtyOn) {
                checkValues()
            } else {
                stopAwty()
            }
        }
    }

    private fun checkValues() {
        val message = messageEdit.text.toString()
        val phoneNumber = phoneEdit.text.toString()
        val interval = timeEdit.text.toString()
        val minutes = interval.toInt()

        when {
            message.isEmpty() -> Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            phoneNumber.isEmpty() -> Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            interval.isEmpty() -> Toast.makeText(this, "Please enter a time interval", Toast.LENGTH_SHORT).show()
            minutes < 1 -> Toast.makeText(this, "Time interval must be an integer greater than 0", Toast.LENGTH_SHORT).show()
            else -> startAwty(message, phoneNumber, minutes)
        }
    }

    private fun startAwty(message: String, phoneNumber: String, minutes: Int) {
        awtyOn = true
        startBtn.text = "Stop"

        val formattedPhone = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country)
        if(receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    Toast.makeText(p0, "${formattedPhone}: ${message}", Toast.LENGTH_SHORT).show()
                }
            }
            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)
        }

        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val minToMillis = minutes * 60000
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), minToMillis.toLong(), pendingIntent)
    }

    private fun stopAwty() {
        unregisterReceiver(receiver)
        receiver = null
        awtyOn = false
        startBtn.text = "Start"
    }
}