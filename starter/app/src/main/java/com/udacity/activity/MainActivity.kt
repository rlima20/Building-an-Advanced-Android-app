package com.udacity.activity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.R
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.custom_button

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadUrl: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radioButtonGlide ->
                    if (checked) {
                        downloadUrl = URL_BUMPTECH
                        Log.i("url", URL_BUMPTECH)
                    }
                R.id.radioButtonUdacity ->
                    if (checked) {
                        downloadUrl = URL_UDACITY
                        Log.i("url", URL_UDACITY)
                    }
                R.id.radioButtonRetrofit ->
                    if (checked) {
                        downloadUrl = URL_RETROFIT
                        Log.i("url", URL_RETROFIT)
                    }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        if (downloadUrl.isBlank()) {
            Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
        } else {
            val request = DownloadManager
                .Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request) // enqueue puts the download request in the queue.
// --------------------------
            val query = DownloadManager.Query()
            query.setFilterById()

            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val downloadStatus =
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(index)) {
                        val i = 0
                    } // handle
                    // downlod success
                    else {
                        val y = 9
                    } // handle download failure
                // sendNotifications(downloadStatus)
            }
        }
    }

    companion object {
        private const val URL_BUMPTECH =
            "https://github.com/bumptech/glide/archive/master.zip"

        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
    }
}
