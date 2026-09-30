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
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
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