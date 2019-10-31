package com.example.vali.pubgithub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.utils.Constants
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.google.gson.Gson

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val owner = SharedPreferencesHelper.getOwner( this)

        handler = Handler()
        val intent = Gson().fromJson(owner, Owner::class.java)?.let {
            Intent(this, RepoListActivity::class.java)
        } ?: run {
            Intent(this, LoginActivity::class.java)
        }
        startActivityWithDelay(intent)
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        finish()
        super.onStop()
    }

    private fun startActivityWithDelay(intent: Intent) {
        handler.postDelayed({
            startActivity(intent)
            finish()
        }, Constants.SPLASH_TIME_OUT)
    }
}
