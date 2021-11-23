package com.ssafy.smartstoredb.ui.splash

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.databinding.ActivitySplashBinding
import com.ssafy.smartstoredb.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    val animaionView: LottieAnimationView by lazy { findViewById(R.id.lottieView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animaionView.playAnimation()
        animaionView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })

//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }, 1500)
    }
}