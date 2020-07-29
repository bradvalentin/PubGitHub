package com.example.vali.pubgithub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.utils.Constants
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val owner = SharedPreferencesHelper.getOwner(this)

        handler = Handler()
        val intent = Gson().fromJson(owner, Owner::class.java)?.let {
            Intent(this, RepoListActivity::class.java)
        } ?: run {
            Intent(this, LoginActivity::class.java)
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                startActivity(intent)
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

    }

    override fun onResume() {
        super.onResume()
        motionLayout.startLayoutAnimation()
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        finish()
        super.onStop()
    }


}
