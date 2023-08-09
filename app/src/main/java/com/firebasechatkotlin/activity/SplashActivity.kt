package com.firebasechatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivityChatBinding
import com.firebasechatkotlin.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    lateinit var activitySplashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      activitySplashBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth?.currentUser

        Handler().postDelayed({
            var intent: Intent?

            intent = if (currentUser != null) {
                Intent(this, ChatUserListActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        },500)

    }

}
