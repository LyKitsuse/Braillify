package com.example.braillify

import androidx.compose.ui.geometry.Offset

class Calibrate {
    /**
     *  Store the Data in a 2x2 Array with 6 Rows and n amount of Columns
     *
     *  run pullCalibratedData()
     *  Store data in 2D Array basisCalibrate
     */

    var calibratedMain: List<List<Offset>> = mutableListOf(
        // Initial Data (Fallback), will be replaced when pullCalibratedData() is ran.
        // The Data exists until the lifecycle of the keyboard until closed.
        // Dot 1 (a)
        mutableListOf(
            Offset(340f, 170f),
            Offset(365f, 170f),
            Offset(365f, 195f),
            Offset(340f, 195f)
        ),
        // Dot 2 (b)
        mutableListOf(
            Offset(340f, 380f),
            Offset(365f, 380f),
            Offset(365f, 405f),
            Offset(340f, 405f)
        ),
        // Dot 3 (c)
        mutableListOf(
            Offset(340f, 550f),
            Offset(365f, 550f),
            Offset(365f, 575f),
            Offset(340f, 575f)
        ),
        // Dot 4 (d)
        mutableListOf(
            Offset(1250f, 170f),
            Offset(1275f, 170f),
            Offset(1275f, 195f),
            Offset(1250f, 195f)
        ),
        // Dot 5 (e)
        mutableListOf(
            Offset(1250f, 380f),
            Offset(1275f, 380f),
            Offset(1275f, 405f),
            Offset(1250f, 405f)
        ),
        // Dot 6 (f)
        mutableListOf(
            Offset(1250f, 550f),
            Offset(1275f, 550f),
            Offset(1275f, 575f),
            Offset(1250f, 575f)
        )
    )

    fun pullCalibratedData(){
        // Pull the x choice (default is [0] if it doesn't exist then calibrate, otherwise fallback to original)
        return
    }

    fun calibrateNew() {
        // Default Values (0,0) on each Offset
        var newCalibration: List<Offset> = mutableListOf(
            // Dots 1 (a)
            Offset(0f,0f),
            // Dots 2 (b)
            Offset(0f,0f),
            // Dots 3 (c)
            Offset(0f,0f),
            // Dots 4 (d)
            Offset(0f,0f),
            // Dots 5 (e)
            Offset(0f,0f),
            // Dots 6 (f)
            Offset(0f,0f))
    }
}