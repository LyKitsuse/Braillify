package com.example.braillify

import android.content.Context
import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.braillify.machineLearningModels.kNearestNeighbor
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class Calibrate {
    private fun getFileName(isLandscape: Boolean): String {
        return if (isLandscape) "calibrated_data_landscape.json" else "calibrated_data_portrait.json"
    }

    fun getDefaultCalibratedData(isLandscape: Boolean = false): List<MutableList<Offset>> {
        return if (!isLandscape) {
            mutableListOf(
                // Portrait default Dot 1 (a) - Left Top
                mutableListOf(
                    Offset(165f, 480f),
                    Offset(190f, 480f),
                    Offset(190f, 505f),
                    Offset(165f, 505f)
                ),
                // Portrait default Dot 2 (b) - Left Middle
                mutableListOf(
                    Offset(165f, 736f),
                    Offset(190f, 736f),
                    Offset(190f, 761f),
                    Offset(165f, 761f)
                ),
                // Portrait default Dot 3 (c) - Left Bottom
                mutableListOf(
                    Offset(165f, 1000f),
                    Offset(190f, 1000f),
                    Offset(190f, 1025f),
                    Offset(165f, 1025f)
                ),
                // Portrait default Dot 4 (d) - Right Top
                mutableListOf(
                    Offset(577f, 530f),
                    Offset(602f, 530f),
                    Offset(602f, 577f),
                    Offset(577f, 555f)
                ),
                // Portrait default Dot 5 (e) - Right Middle
                mutableListOf(
                    Offset(555f, 700f),
                    Offset(580f, 700f),
                    Offset(580f, 725f),
                    Offset(555f, 725f)
                ),
                // Portrait default Dot 6 (f) - Right Bottom
                mutableListOf(
                    Offset(548f, 983f),
                    Offset(573f, 983f),
                    Offset(573f, 1008f),
                    Offset(548f, 1008f)
                )
            )
        } else {
            mutableListOf(
                // Landscape default Dot 1 (a)
                mutableListOf(
                    Offset(340f, 170f),
                    Offset(365f, 170f),
                    Offset(365f, 195f),
                    Offset(340f, 195f)
                ),
                // Landscape default Dot 2 (b)
                mutableListOf(
                    Offset(340f, 380f),
                    Offset(365f, 380f),
                    Offset(365f, 405f),
                    Offset(340f, 405f)
                ),
                // Landscape default Dot 3 (c)
                mutableListOf(
                    Offset(340f, 550f),
                    Offset(365f, 550f),
                    Offset(365f, 575f),
                    Offset(340f, 575f)
                ),
                // Landscape default Dot 4 (d)
                mutableListOf(
                    Offset(1250f, 170f),
                    Offset(1275f, 170f),
                    Offset(1275f, 195f),
                    Offset(1250f, 195f)
                ),
                // Landscape default Dot 5 (e)
                mutableListOf(
                    Offset(1250f, 380f),
                    Offset(1275f, 380f),
                    Offset(1275f, 405f),
                    Offset(1250f, 405f)
                ),
                // Landscape default Dot 6 (f)
                mutableListOf(
                    Offset(1250f, 550f),
                    Offset(1275f, 550f),
                    Offset(1275f, 575f),
                    Offset(1250f, 575f)
                )
            )
        }
    }

    // Store the Data in a 2D List with 6 Rows (Dots 1 to 6) and n amount of Columns
    var calibratedMain: List<MutableList<Offset>> = getDefaultCalibratedData(false)

    // Basis Braille Cell: First Six Dots
    var newCalibration: List<Offset> = mutableListOf(
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f),
        Offset(0f, 0f)
    )

    fun pullCalibratedData(context: Context, isLandscape: Boolean = false): List<MutableList<Offset>>? {
        try {
            val fileName = getFileName(isLandscape)
            var file = File(context.filesDir, fileName)
            if (!file.exists() && !isLandscape) {
                // Fallback to legacy calibrated_data.json for portrait
                val legacyFile = File(context.filesDir, "calibrated_data.json")
                if (legacyFile.exists()) {
                    file = legacyFile
                }
            }
            if (!file.exists()) {
                calibratedMain = getDefaultCalibratedData(isLandscape)
                return null
            }
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
                if (newList.any { it.isEmpty() }) {
                    calibratedMain = getDefaultCalibratedData(isLandscape)
                    return null
                }
                calibratedMain = newList
                Log.d("Calibrate", "Successfully pulled calibrated data for landscape=$isLandscape")
                return newList
            }
        } catch (e: Exception) {
            Log.e("Calibrate", "Error pulling calibrated data", e)
        }
        calibratedMain = getDefaultCalibratedData(isLandscape)
        return null
    }

    fun saveCalibratedData(context: Context, data: List<List<Offset>>, isLandscape: Boolean = false) {
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
            val fileName = getFileName(isLandscape)
            val file = File(context.filesDir, fileName)
            file.writeText(rootArray.toString())
            Log.d("Calibrate", "Saved calibrated data for landscape=$isLandscape to ${file.absolutePath}")
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

    fun exitCalibration(context: Context, isLandscape: Boolean = false) {
        Keyboard.isCalibrating = false
        Keyboard.calibrationStep = 0
        newCalibration = mutableListOf(
            Offset(0f, 0f), Offset(0f, 0f), Offset(0f, 0f),
            Offset(0f, 0f), Offset(0f, 0f), Offset(0f, 0f)
        )
        if (pullCalibratedData(context, isLandscape) == null) {
            calibratedMain = getDefaultCalibratedData(isLandscape)
        }
    }

    fun recordInitialDot(dotIndex: Int, point: Offset) {
        if (dotIndex in 0..5) {
            val mutableNew = newCalibration.toMutableList()
            mutableNew[dotIndex] = point
            newCalibration = mutableNew

            if (dotIndex == 5) {
                calibratedMain = mutableListOf(
                    mutableListOf(newCalibration[0]),
                    mutableListOf(newCalibration[1]),
                    mutableListOf(newCalibration[2]),
                    mutableListOf(newCalibration[3]),
                    mutableListOf(newCalibration[4]),
                    mutableListOf(newCalibration[5])
                )
            }
        }
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