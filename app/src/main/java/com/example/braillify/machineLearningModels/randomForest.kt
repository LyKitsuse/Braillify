package com.example.braillify.machineLearningModels

import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot
import kotlin.random.Random

class randomForest(
    private val numTrees: Int = 51,        // odd number avoids ties
    private val maxDepth: Int = 8,
    private val minSamplesSplit: Int = 2,
    private val featuresPerSplit: Int = 3, // ~sqrt(8 features)
    seed: Int = 42
) {
    private val labels = listOf("a", "b", "c", "d", "e", "f")

    private class Sample(val features: FloatArray, val label: String)

    private sealed class Node {
        class Leaf(val label: String) : Node()
        class Split(
            val feature: Int,
            val threshold: Float,
            val left: Node,
            val right: Node
        ) : Node()
    }

    private val rng = Random(seed)
    private var trees = listOf<Node>()
    private var centroids = listOf<Offset>()
    private var trainedOn: List<List<Offset>>? = null

    /**
     * epsilon   = current touch
     * pointsRef = calibration points, pointsRef[0] = "a" ... pointsRef[5] = "f"
     * Returns "a".."f", or "?" if the calibration data is incomplete.
     */
    fun randomForestAlgo(epsilon: Offset, pointsRef: List<List<Offset>>): String {
        if (pointsRef.size < 6 || pointsRef.take(6).any { it.isEmpty() }) return "?"

        // Train only when calibration data changes
        if (trainedOn != pointsRef) train(pointsRef.take(6))

        val f = features(epsilon)
        val votes = HashMap<String, Int>()
        for (tree in trees) {
            val label = walk(tree, f)
            votes[label] = (votes[label] ?: 0) + 1
        }
        return votes.maxByOrNull { it.value }!!.key
    }

    // ------------------------------------------------------------ training

    private fun train(groups: List<List<Offset>>) {
        centroids = groups.map { g ->
            Offset(
                g.map { it.x }.average().toFloat(),
                g.map { it.y }.average().toFloat()
            )
        }

        val samples = groups.flatMapIndexed { i, g ->
            g.map { Sample(features(it), labels[i]) }
        }

        trees = List(numTrees) {
            // Bootstrap: draw with replacement, same size as the original set
            val bootstrap = List(samples.size) { samples[rng.nextInt(samples.size)] }
            buildTree(bootstrap, 0)
        }

        trainedOn = groups.map { it.toList() } // snapshot to detect later changes
    }

    // Features: x, y, and distance to each dot's calibrated centroid (8 total)
    private fun features(p: Offset): FloatArray {
        val out = FloatArray(2 + centroids.size)
        out[0] = p.x
        out[1] = p.y
        for (i in centroids.indices) {
            out[2 + i] = hypot(p.x - centroids[i].x, p.y - centroids[i].y)
        }
        return out
    }

    private fun buildTree(data: List<Sample>, depth: Int): Node {
        val lbls = data.map { it.label }

        if (depth >= maxDepth || data.size < minSamplesSplit || lbls.distinct().size == 1) {
            return Node.Leaf(majority(lbls))
        }

        // Random subset of features considered at this split
        val numFeatures = data[0].features.size
        val candidates = (0 until numFeatures).shuffled(rng)
            .take(featuresPerSplit.coerceAtMost(numFeatures))

        var bestFeature = -1
        var bestThreshold = 0f
        var bestScore = Double.MAX_VALUE

        for (f in candidates) {
            val sorted = data.sortedBy { it.features[f] }
            val left = HashMap<String, Int>()
            val right = lbls.groupingBy { it }.eachCount().toMutableMap()

            for (i in 0 until sorted.size - 1) {
                val l = sorted[i].label
                left[l] = (left[l] ?: 0) + 1
                right[l] = (right[l] ?: 0) - 1

                val a = sorted[i].features[f]
                val b = sorted[i + 1].features[f]
                if (a == b) continue // can't split between identical values

                val nLeft = i + 1
                val nRight = sorted.size - nLeft
                val score = (nLeft * gini(left, nLeft) + nRight * gini(right, nRight)) / sorted.size

                if (score < bestScore) {
                    bestScore = score
                    bestFeature = f
                    bestThreshold = (a + b) / 2f
                }
            }
        }

        if (bestFeature == -1) return Node.Leaf(majority(lbls))

        val (leftData, rightData) = data.partition { it.features[bestFeature] <= bestThreshold }
        if (leftData.isEmpty() || rightData.isEmpty()) return Node.Leaf(majority(lbls))

        return Node.Split(
            bestFeature,
            bestThreshold,
            buildTree(leftData, depth + 1),
            buildTree(rightData, depth + 1)
        )
    }

    private fun gini(counts: Map<String, Int>, total: Int): Double {
        if (total == 0) return 0.0
        var sumSq = 0.0
        for (c in counts.values) {
            val p = c.toDouble() / total
            sumSq += p * p
        }
        return 1.0 - sumSq
    }

    private fun majority(l: List<String>): String =
        l.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key

    // ---------------------------------------------------------- prediction

    private fun walk(node: Node, f: FloatArray): String = when (node) {
        is Node.Leaf -> node.label
        is Node.Split ->
            if (f[node.feature] <= node.threshold) walk(node.left, f) else walk(node.right, f)
    }
}