package com.udacity.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.content_detail.buttonOk
import kotlinx.android.synthetic.main.content_detail.textViewFileName
import kotlinx.android.synthetic.main.content_detail.textViewStatusValue

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        setupFields()
        setupListeners()
    }

    private fun setupFields() {
        textViewFileName.text = intent.getStringExtra(FILE_NAME_PARAMETER)
        textViewStatusValue.let {
            it.text = intent.getStringExtra(STATUS_PARAMETER)
            if (textViewStatusValue.text == getString(R.string.string_status_failed)) {
                it.setTextColor(getColor(R.color.failStatus))
            }
        }
    }

    private fun setupListeners() {
        buttonOk.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
            finish()
        }
    }

    companion object {
        private const val FILE_NAME_PARAMETER = "file_name"
        private const val STATUS_PARAMETER = "status"

        fun createIntent(context: Context, fileName: String, status: String): Intent {
            return Intent(context, DetailActivity::class.java)
                .putExtra(FILE_NAME_PARAMETER, fileName)
                .putExtra(STATUS_PARAMETER, status)
        }
    }
}
