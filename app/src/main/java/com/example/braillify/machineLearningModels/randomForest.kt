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

        // Safe access helpers to avoid NullPointerExceptions during calculations
        fun getX(i: Int): Float = randomOffsets[i]?.x ?: 0f
        fun getY(i: Int): Float = randomOffsets[i]?.y ?: 0f

        // find distance between outlier of (abc) and outlier of (efg)
        var outlierA: Offset = randomOffsets[0] ?: Offset.Zero
        var outlierB: Offset = randomOffsets[3] ?: Offset.Zero

        var tempDist = 0f
        var currDist = 999999f

        for (i in 0 until 3) {
            for (j in 3 until 6) {
                tempDist = distanceFormula(getX(i), getY(i), getX(j), getY(j))
                if (currDist > tempDist) {
                    currDist = tempDist
                    outlierA = randomOffsets[i] ?: Offset.Zero
                    outlierB = randomOffsets[j] ?: Offset.Zero
                }
            }
        }

        val decisionOffset: Offset = Offset(
            x = (outlierA.x + outlierB.x) / 2f,
            y = (outlierA.y + outlierB.y) / 2f
        )

        // Your exact nested decision tree logic:
        val predictedLabel = if (getX(0) > getX(1)) {
            if (getY(0) > getY(1)) {
                "a"
            } else {
                if (getY(1) > getY(2)) {
                    "b"
                } else {
                    "c"
                }
            }
        } else {
            if (getY(3) > getY(4)) {
                "d"
            } else {
                if (getY(4) > getY(5)) {
                    "e"
                } else {
                    "f"
                }
            }
        }

        return predictedLabel
    }
}