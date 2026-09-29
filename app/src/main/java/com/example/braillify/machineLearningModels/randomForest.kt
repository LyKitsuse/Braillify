package com.example.braillify.machineLearningModels

import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot

class randomForest {
    fun distanceFormula(X: Float, Y: Float, XP: Float, YP: Float): Float {
        return hypot(XP - X, YP - Y)
    }

    fun randomForestAlgo(epsilon: Offset, pointsRef: List<List<Offset>>): String {
        val randomOffsets: Array<Offset?> = arrayOfNulls<Offset>(6)

        for (i in 0 until minOf(6, pointsRef.size)) {
            val dotGroup = pointsRef[i]
            if (dotGroup.isNotEmpty()) {
                randomOffsets[i] = dotGroup.random() // Pick a random Offset from the sublist
            }
        }

        // find distance between outlier of (abc) and outlier of (efg) then check if the epsilon offset x is > or <
        var outlierA: Offset? = null
        var outlierB: Offset? = null

        var tempDist = 0f
        var currDist = 999999f

        for (i in 0 until 3) {
            for (j in 3 until 6) {
                if (randomOffsets[i] != null && randomOffsets[j] != null) {
                    tempDist = distanceFormula(
                        randomOffsets[i]!!.x,
                        randomOffsets[i]!!.y,
                        randomOffsets[j]!!.x,
                        randomOffsets[j]!!.y
                    )
                    if (currDist > tempDist) {
                        currDist = tempDist
                        outlierA = randomOffsets[i]
                        outlierB = randomOffsets[j]
                    }
                }
            }
        }

        // Safely compute decision offset using non-null fallbacks if outliers weren't found
        val decisionOffset: Offset = if (outlierA != null && outlierB != null) {
            (outlierA + outlierB) / 2f
        } else {
            Offset.Zero
        }

        // Fixed nested when-expression syntax and Offset comparison property access (.y)
        val predictedLabel = when {
            (randomOffsets[0]?.y ?: 0f) > (randomOffsets[1]?.y ?: 0f) -> {
                when {
                    (randomOffsets[0]?.y ?: 0f) > (randomOffsets[1]?.y ?: 0f) -> "a"
                    (randomOffsets[1]?.y ?: 0f) > (randomOffsets[2]?.y ?: 0f) -> "b"
                    else -> "c"
                }
            }
            else -> {
                when {
                    (randomOffsets[3]?.y ?: 0f) > (randomOffsets[4]?.y ?: 0f) -> "d"
                    (randomOffsets[4]?.y ?: 0f) > (randomOffsets[5]?.y ?: 0f) -> "e"
                    else -> "f"
                }
            }
        }

        return predictedLabel
    }
}