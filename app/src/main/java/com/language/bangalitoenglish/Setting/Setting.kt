package com.language.bangalitoenglish.Setting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.language.bangalitoenglish.R
import kotlinx.android.synthetic.main.activity_setting.*

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ee2e44")))


        share_btn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val app_url = "https://play.google.com/store/apps/details?id=$packageName"
            shareIntent.putExtra(Intent.EXTRA_TEXT, app_url)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        rate_btn.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName"))
            )
        }
    }
}