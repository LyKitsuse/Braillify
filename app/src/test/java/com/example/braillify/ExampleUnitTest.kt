package com.example.braillify

import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testPortraitDefaultDotsOrientation() {
        val calibrate = Calibrate()
        val portraitData = calibrate.getDefaultCalibratedData(isLandscape = false)
        
        val leftDot1X = portraitData[0][0].x
        val rightDot4X = portraitData[3][0].x
        
        // Left column (Dots 1, 2, 3) must have smaller X than Right column (Dots 4, 5, 6)
        assertTrue("Left dot X ($leftDot1X) should be less than Right dot X ($rightDot4X)", leftDot1X < rightDot4X)
    }

    @Test
    fun testPortraitModelPrediction() {
        val keyboard = Keyboard()
        val calibrate = Calibrate()
        val portraitData = calibrate.getDefaultCalibratedData(isLandscape = false)

        // Tap near Dot 1 (a) (e.g., Offset(165f, 480f))
        val tapPoint = androidx.compose.ui.geometry.Offset(165f, 480f)
        val rawOutput = keyboard.runModel(listOf(tapPoint), portraitData)
        val brailleOutput = keyboard.processRawBraille(rawOutput, BrailleDictionary)

        assertEquals("100000", rawOutput)
        assertEquals("a", brailleOutput)
    }

    @Test
    fun generateCalibrationJsonOnLaptop() {
        val calibrate = Calibrate()
        val data = calibrate.calibratedMain

        val sb = StringBuilder()
        sb.append("[\n")
        data.forEachIndexed { index, dotGroup ->
            sb.append("  [\n")
            dotGroup.forEachIndexed { ptIndex, offset ->
                sb.append("    {\"x\": ${offset.x}, \"y\": ${offset.y}}")
                if (ptIndex < dotGroup.size - 1) sb.append(",")
                sb.append("\n")
            }
            sb.append("  ]")
            if (index < data.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("]\n")

        val file = File("../calibrated_data.json")
        file.writeText(sb.toString())
        println("Generated calibration JSON at: ${file.absolutePath}")
        assertTrue(file.exists())
    }
}