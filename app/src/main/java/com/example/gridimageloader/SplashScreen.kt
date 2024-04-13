package com.example.gridimageloader

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val tv_splash: TextView = findViewById(R.id.tv_splash)
        animator(tv_splash)
        moveTonext()


    }

    private fun animator(tv_splash: TextView) {
        val scaleXAnimator = ObjectAnimator.ofFloat(tv_splash, "scaleX", 1.0f, 1.5f, 1.0f)
        scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
        scaleXAnimator.duration = Utils.TEXT_ANIMATION_DURATION
        scaleXAnimator.start()

        val scaleYAnimator = ObjectAnimator.ofFloat(tv_splash, "scaleY", 1.0f, 1.5f, 1.0f)
        scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
        scaleYAnimator.duration = Utils.TEXT_ANIMATION_DURATION
        scaleYAnimator.start()
    }

    private fun moveTonext() {
        Thread {
            try {
                Thread.sleep(Utils.SPLASH_DURATION)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}