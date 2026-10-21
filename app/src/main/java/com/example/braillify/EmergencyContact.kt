package com.example.braillify

import android.util.Log

class EmergencyContact {
    var count = 0
    var state = 0 // 0 = default, 1 = wait swipe up, 2 = wait swipe down

    fun checkIfSOS(out: String) {
        if (out == "111111") {
            count++
            Log.d("DEBUG", "Count: $count")
            if (count >= 5) {
                state = 1
            }
        } else {
            Log.d("DEBUG", "Count Reset: $count")
            count = 0
            state = 0
        }
    }

    fun handleSwipe(totalDx: Float, totalDy: Float): Boolean {
        if (state == 0) return false

        val isVertical = kotlin.math.abs(totalDy) > kotlin.math.abs(totalDx)
        val isUp = isVertical && totalDy < 0
        val isDown = isVertical && totalDy > 0

        if (state == 1 && isUp) {
            state = 2
            Log.d("DEBUG", "Swiped UP")
            return true
        }

        if (state == 2 && isDown) {
            count = 0
            state = 0
            Log.d("DEBUG", "Call Number")
            return true
        }

        state = 0
        return true
    }
}
