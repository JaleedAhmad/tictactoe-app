package com.example.tictactoe.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object GameFeedbackManager {

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun playMoveFeedback(context: Context, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSound(context, android.media.AudioManager.FX_KEY_CLICK)
        }
        if (vibrationEnabled) {
            vibrate(context, 30)
        }
    }

    fun playWinFeedback(context: Context, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSound(context, android.media.AudioManager.FX_FOCUS_NAVIGATION_UP)
        }
        if (vibrationEnabled) {
            vibrate(context, 200)
        }
    }

    fun playDrawFeedback(context: Context, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSound(context, android.media.AudioManager.FX_FOCUS_NAVIGATION_DOWN)
        }
        if (vibrationEnabled) {
            vibrate(context, 100)
        }
    }

    private fun playSound(context: Context, effectType: Int) {
        try {
            val uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            val mp = MediaPlayer.create(context, uri)
            mp?.setOnCompletionListener { it.release() }
            mp?.start()
        } catch (_: Exception) {}
    }

    private fun vibrate(context: Context, durationMs: Long) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}

