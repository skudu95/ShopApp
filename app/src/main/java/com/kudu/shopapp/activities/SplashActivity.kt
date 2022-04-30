package com.kudu.shopapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kudu.shopapp.databinding.ActivitySplashBinding
import com.kudu.shopapp.firestore.Firestore

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                val currentUserID = Firestore().getCurrentUserId()

                if (currentUserID.isNotEmpty()) {
                    // Launch dashboard screen.
                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                } else {
                    // Launch the Login Activity
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            },
            2500
        )


    }
}