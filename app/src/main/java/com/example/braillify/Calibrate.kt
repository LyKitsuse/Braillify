package com.example.braillify

import android.content.Context
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.braillify.machineLearningModels.kNearestNeighbor
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class Calibrate {
    /**
     * Store the Data in a 2D List with 6 Rows (Dots 1 to 6) and n amount of Columns
     */
    var calibratedMain: List<MutableList<Offset>> = mutableListOf(
        // Initial Data (Fallback), will be replaced when pullCalibratedData() is run.
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

    // Basis Braille Cell: First Six Dots
    var newCalibration: List<Offset> = mutableListOf(
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f)
    )

    fun pullCalibratedData(context: Context): List<MutableList<Offset>>? {
        try {
            val file = File(context.filesDir, "calibrated_data.json")
            if (!file.exists()) return null
            val jsonString = file.readText()
            val rootArray = JSONArray(jsonString)
            if (rootArray.length() == 6) {
                val newList = mutableListOf<MutableList<Offset>>()
                for (i in 0 until rootArray.length()) {
                    val groupArray = rootArray.getJSONArray(i)
                    val groupList = mutableListOf<Offset>()
                    for (j in 0 until groupArray.length()) {
                        val pointObj = groupArray.getJSONObject(j)
                        val x = pointObj.getDouble("x").toFloat()
                        val y = pointObj.getDouble("y").toFloat()
                        groupList.add(Offset(x, y))
                    }
                    newList.add(groupList)
                }
                calibratedMain = newList
                Log.d("Calibrate", "Successfully pulled calibrated data")
                return newList
            }
        } catch (e: Exception) {
            Log.e("Calibrate", "Error pulling calibrated data", e)
        }
        return null
    }

    fun saveCalibratedData(context: Context, data: List<List<Offset>>) {
        try {
            val rootArray = JSONArray()
            for (dotGroup in data) {
                val groupArray = JSONArray()
                for (offset in dotGroup) {
                    val pointObj = JSONObject().apply {
                        put("x", offset.x.toDouble())
                        put("y", offset.y.toDouble())
                    }
                    groupArray.put(pointObj)
                }
                rootArray.put(groupArray)
            }
            val file = File(context.filesDir, "calibrated_data.json")
            file.writeText(rootArray.toString())
            Log.d("Calibrate", "Saved calibrated data to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("Calibrate", "Error saving calibrated data", e)
        }
    }

    fun calibrateNew() {
        newCalibration = mutableListOf(
            Offset(0f, 0f), Offset(0f, 0f), Offset(0f, 0f),
            Offset(0f, 0f), Offset(0f, 0f), Offset(0f, 0f)
        )
        Keyboard.isCalibrating = true
        Keyboard.calibrationStep = 0
    }

    fun processInitialCell(taps: List<Offset>): Boolean {
        if (taps.size < 6) return false
        val sortedByX = taps.take(6).sortedBy { it.x }
        val leftCol = sortedByX.take(3).sortedBy { it.y }
        val rightCol = sortedByX.takeLast(3).sortedBy { it.y }

        val dot1 = leftCol[0]
        val dot2 = leftCol[1]
        val dot3 = leftCol[2]
        val dot4 = rightCol[0]
        val dot5 = rightCol[1]
        val dot6 = rightCol[2]

        newCalibration = listOf(dot1, dot2, dot3, dot4, dot5, dot6)

        calibratedMain = mutableListOf(
            mutableListOf(dot1),
            mutableListOf(dot2),
            mutableListOf(dot3),
            mutableListOf(dot4),
            mutableListOf(dot5),
            mutableListOf(dot6)
        )
        return true
    }

    fun appendCalibratedPoint(p: Offset) {
        val knn = kNearestNeighbor()
        val pointsRef = newCalibration.map { listOf(it) }
        val predictedLabel = knn.kNN(p, pointsRef)
        val dotIndex = when (predictedLabel) {
            "a" -> 0
            "b" -> 1
            "c" -> 2
            "d" -> 3
            "e" -> 4
            "f" -> 5
            else -> 0
        }
        calibratedMain[dotIndex].add(p)
    }
}
