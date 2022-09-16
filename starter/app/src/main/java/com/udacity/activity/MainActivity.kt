package com.udacity.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.ButtonState
import com.udacity.R
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.custom_button

class MainActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager
    private var fileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()
        setupClickListener()
    }

    private fun setupClickListener() {
        radioGroup = findViewById(R.id.radioGroupDownload)
        custom_button.setOnClickListener {
            val url = when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> URL_BUMPTECH
                R.id.radioButtonUdacity -> URL_UDACITY
                R.id.radioButtonRetrofit -> URL_RETROFIT
                else -> ""
            }

            if (url.isEmpty()) {
                Toast.makeText(this, R.string.string_toast_message, Toast.LENGTH_SHORT).show()
            } else {
                val radioButton = findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                fileName = radioButton.text.toString()

                custom_button.setState(ButtonState.Loading)
                download(url)
            }
        }
    }

    private fun createChannel() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            custom_button.complete()
            val id = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query().setFilterById(id)
            val cursor = downloadManager.query(query)

            var status = getString(R.string.string_status_failed)
            if (cursor.moveToFirst()) {
                val statusCode = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (statusCode == DownloadManager.STATUS_SUCCESSFUL) {
                    status = getString(R.string.string_status_failed)
                }
            }

            val action = buildNotificationAction(status)
            val builder = context?.let { builderContext ->
                NotificationCompat.Builder(
                    builderContext,
                    CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                    .setContentTitle(getString(R.string.string_notification_title))
                    .setContentText(getString(R.string.string_notification_description))
                    .addAction(action)
            }

            notificationManager.notify(NOTIFICATION_ID, builder?.build())
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun buildNotificationAction(status: String): NotificationCompat.Action {
        val intent = DetailActivity.createIntent(applicationContext, fileName, status)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action(
            0,
            getString(R.string.string_notification_button),
            pendingIntent
        )
    }

    private fun download(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(getString(R.string.string_app_name))
            .setDescription(getString(R.string.string_app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    companion object {
        private const val URL_BUMPTECH =
            "https://github.com/bumptech/glide/archive/master.zip"

        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
    }
}
