package com.example.braillify.machineLearningModels

import androidx.compose.ui.geometry.Offset
import com.example.braillify.BrailleDictionary
import kotlin.math.hypot

class kNearestNeighbor {
    // k = 13 > 11 > 9 > 7 > 5 > 3
    // Equivalent to: euclidean_distance(point1, point2)
    fun distanceFormula(X: Float, Y: Float, XP: Float, YP: Float): Float {
        return hypot(XP - X, YP - Y)
    }

    // Data class to mirror distances.append((dist, training_labels[i]))
    data class Neighbor(val distance: Float, val label: String)

    fun kNN(epsilon: Offset, pointsRef: List<List<Offset>>): String {
        val k = 1

        // Dot group labels corresponding to pointsL2D indices 0 to 5
        val labels = listOf("a", "b", "c", "d", "e", "f")

        // array of distances
        val distances = mutableListOf<Neighbor>()

        // Flatten training_data and collect distances with their corresponding labels
        for ((index, dotGroup) in pointsRef.withIndex()) {
            val currentLabel = labels[index]

            for (refPoint in dotGroup) {
                // dist = euclidean_distance(test_point, training_data[i])
                val dist = distanceFormula(refPoint.x, refPoint.y, epsilon.x, epsilon.y)

                // distances.append((dist, training_labels[i]))
                distances.add(Neighbor(dist, currentLabel))
            }
        }

        // distances.sort(key=lambda x: x[0])
        distances.sortBy { it.distance }

        // k_nearest_labels = [label for _, label in distances[:k]]
        val kNearestLabels = distances.take(k).map { it.label }

        // Counter(k_nearest_labels).most_common(1)[0][0]
        val predictedLabel = kNearestLabels
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "unknown"

        return predictedLabel
    }
}