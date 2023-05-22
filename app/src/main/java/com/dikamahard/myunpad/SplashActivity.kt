package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBar

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivSplash = findViewById<ImageView>(R.id.iv_splash)
        ivSplash.alpha = 0f
        ivSplash.animate().setDuration(1500).alpha(1f).withEndAction{
            val intentStart = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intentStart)
            supportActionBar?.hide()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }


    }
}