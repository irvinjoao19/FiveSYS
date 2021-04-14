package com.fivesys.alphamanufacturas.fivesys.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemClock.sleep(1500)
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}