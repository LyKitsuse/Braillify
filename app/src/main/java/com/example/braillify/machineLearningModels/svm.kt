package com.example.braillify.machineLearningModels

import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot

class svm {
    private val labels = listOf("a", "b", "c", "d", "e", "f")

    data class BoundaryInfo(
        val closest_i: Offset,
        val closest_j: Offset,
        val distance: Float,
        val midpoint: Offset
    )

    data class LineInfo(
        val a: Float,
        val b: Float,
        val c: Float,
        val i_sign: Int,
        val midpoint: Offset,
        val closest_i: Offset,
        val closest_j: Offset
    )

    private var cachedLines: Map<Pair<Int, Int>, LineInfo>? = null
    private var trainedOn: List<List<Offset>>? = null

    // EUCLIDEAN DISTANCE
    fun euclidean(a: Offset, b: Offset): Float {
        return hypot(a.x - b.x, a.y - b.y)
    }

    // SMALLEST DISTANCE BETWEEN EACH PAIR OF DOTS + MIDPOINT AS BOUNDARY POINT
    fun compute_boundaries(calibration_data: List<List<Offset>>): Map<Pair<Int, Int>, BoundaryInfo> {
        val boundaries = mutableMapOf<Pair<Int, Int>, BoundaryInfo>()
        val dots = (1..calibration_data.size).toList()

        for (a_i in dots.indices) {
            for (b_i in a_i + 1 until dots.size) {
                val i = dots[a_i]
                val j = dots[b_i]

                var best_p: Offset? = null
                var best_q: Offset? = null
                var best_d = Float.MAX_VALUE

                val pointsI = calibration_data.getOrNull(i - 1) ?: emptyList()
                val pointsJ = calibration_data.getOrNull(j - 1) ?: emptyList()

                for (p in pointsI) {
                    for (q in pointsJ) {
                        val d = euclidean(p, q)
                        if (d < best_d) {
                            best_d = d
                            best_p = p
                            best_q = q
                        }
                    }
                }

                if (best_p != null && best_q != null) {
                    val midpoint = Offset(
                        (best_p.x + best_q.x) / 2f,
                        (best_p.y + best_q.y) / 2f
                    )

                    boundaries[Pair(i, j)] = BoundaryInfo(
                        closest_i = best_p,
                        closest_j = best_q,
                        distance = best_d,
                        midpoint = midpoint
                    )
                }
            }
        }

        return boundaries
    }

    // COMPUTING PERPENDICULAR BISECTOR LINE USING NORMAL VECTOR
    fun compute_line(boundaries: Map<Pair<Int, Int>, BoundaryInfo>): Map<Pair<Int, Int>, LineInfo> {
        val lines = mutableMapOf<Pair<Int, Int>, LineInfo>()

        for ((pair, info) in boundaries) {
            val p = info.closest_i
            val q = info.closest_j
            val M = info.midpoint

            // Normal vector of the perpendicular bisector = direction of pq
            val a = q.x - p.x
            val b = q.y - p.y

            // Constant term: force line through M
            val c = -(a * M.x + b * M.y)

            // Determine which sign corresponds to dot i's side.
            // Plug p into f; if f(p) > 0, dot i's side is positive.
            val f_p = a * p.x + b * p.y + c
            val i_sign = if (f_p > 0) 1 else -1

            lines[pair] = LineInfo(
                a = a,
                b = b,
                c = c,
                i_sign = i_sign,
                midpoint = M,
                closest_i = p,
                closest_j = q
            )
        }

        return lines
    }

    fun evaluate_line(line: LineInfo, point: Offset): Float {
        // Sign indicates which side of the line the point lies on.
        val a = line.a
        val b = line.b
        val c = line.c
        return a * point.x + b * point.y + c
    }

    fun on_i_side(line: LineInfo, point: Offset): Boolean {
        // True if point is on dot i's side of this boundary line.
        val valExpr = evaluate_line(line, point)
        return (valExpr * line.i_sign) > 0
    }

    // CLASSIFY A TAP USING THE LINE BOUNDARIES
    fun classify_tap(tap: Offset, lines: Map<Pair<Int, Int>, LineInfo>, calibration_data: List<List<Offset>>): Pair<Int, Map<Int, Int>> {
        // For each boundary line, the tap votes for the dot whose side it falls on. After all lines, the dot with the most votes wins.
        val dots = (1..calibration_data.size).toList()
        val votes = dots.associateWith { 0 }.toMutableMap()

        for ((pair, line) in lines) {
            val (i, j) = pair
            if (on_i_side(line, tap)) {
                votes[i] = votes[i]!! + 1
            } else {
                votes[j] = votes[j]!! + 1
            }
        }

        val best_dot = votes.maxByOrNull { it.value }?.key ?: 1
        return Pair(best_dot, votes)
    }

    fun svmAlgo(epsilon: Offset, pointsRef: List<List<Offset>>): String {
        if (pointsRef.size < 6 || pointsRef.take(6).any { it.isEmpty() }) {
            return "a"
        }

        val cleanPoints = pointsRef.take(6)
        if (trainedOn != cleanPoints) {
            val boundaries = compute_boundaries(cleanPoints)
            cachedLines = compute_line(boundaries)
            trainedOn = cleanPoints.map { it.toList() }
        }

        val lines = cachedLines ?: return "a"
        val (bestDot, _) = classify_tap(epsilon, lines, cleanPoints)
        val idx = bestDot - 1

        Log.d("DEBUG", if (idx in labels.indices) labels[idx] else "a")
        return if (idx in labels.indices) labels[idx] else "a"
    }
}
