package com.example.braillify

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.ui.theme.BraillifyTheme
import com.example.braillify.machineLearningModels.kNearestNeighbor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

class Keyboard : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val mViewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = mViewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var isCapital: Boolean = false
    private var isCapitalOnce: Boolean = false
    private var isNumeral: Boolean = false

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return true
    }

    override fun onCreateInputView(): View {
        // Essential: Bind owners to the Window DecorView to prevent crashes in Service context
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@Keyboard)
            setViewTreeViewModelStoreOwner(this@Keyboard)
            setViewTreeSavedStateRegistryOwner(this@Keyboard)

            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS

            accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                    // Do not call super to prevent any default info
                    info.isVisibleToUser = false
                    info.isFocusable = false
                    info.isImportantForAccessibility = false
                    info.className = View::class.java.name
                    info.packageName = packageName
                }
                
                override fun dispatchPopulateAccessibilityEvent(host: View, event: AccessibilityEvent): Boolean {
                    return true // Consume
                }
            }

            // 3. Consume Hover events to stop TalkBack exploration
            setOnHoverListener { _, _ -> true }
            
            // 4. Recursive bypass on attach
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    var current = v.parent
                    while (current is View) {
                        current.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                        current.isFocusable = false
                        current.isClickable = false
                        current = current.parent
                    }
                }
                override fun onViewDetachedFromWindow(v: View) {}
            })
        }

        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        composeView.setContent {
            BraillifyTheme {
                val configuration = LocalConfiguration.current
                var statusLabel by remember { mutableStateOf("Ready") }
                var modeLabel by remember { mutableStateOf("Lowercase") }
                val taps = remember { mutableStateListOf<Offset>() }
                var tapEq by remember { mutableStateOf("Tap Anywhere!") }

                if ((configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) || configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x804B0082))
                            .clearAndSetSemantics { }
                    ) {
                        // Minimalistic Header for Status

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        val initialDown = awaitFirstDown()
                                        val dict = BrailleDictionary
                                        val cal = Calibrate()

                                        // Give a tiny fraction of a second (50ms) for all other fingers in the chord to land
                                        withTimeoutOrNull(50L) {
                                            while (true) {
                                                awaitPointerEvent()
                                            }
                                        }

                                        // Grab EVERY finger touching the screen right now
                                        val currentPointers = currentEvent.changes.filter { it.pressed }

                                        // Save all finger positions at once
                                        taps.clear()
                                        for (pointer in currentPointers) {
                                            taps.add(pointer.position)
                                            Log.d("TAP", "Tap at: X=${pointer.position.x}, Y=${pointer.position.y}")
                                        }

                                        val pointerStarts = mutableMapOf<Long, Offset>()
                                        val pointerEnds = mutableMapOf<Long, Offset>()
                                        
                                        // Initialize pointerStarts with what we captured in the chord window
                                        for (pointer in currentPointers) {
                                            pointerStarts[pointer.id.value] = pointer.position
                                            pointerEnds[pointer.id.value] = pointer.position
                                        }
                                        // Ensure initialDown is included if it was missed or released quickly (unlikely but safe)
                                        if (!pointerStarts.containsKey(initialDown.id.value)) {
                                            pointerStarts[initialDown.id.value] = initialDown.position
                                        }

                                        var maxFingers = pointerStarts.size

                                        // Tracking loop: Record movement and finger count until all lift
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val currentCount = event.changes.count { it.pressed }
                                            if (currentCount > maxFingers) maxFingers = currentCount

                                            for (change in event.changes) {
                                                val id = change.id.value
                                                if (change.pressed) {
                                                    if (!pointerStarts.containsKey(id)) pointerStarts[id] =
                                                        change.position
                                                    pointerEnds[id] = change.position
                                                }
                                            }
                                            if (event.changes.all { !it.pressed }) break
                                        }

                                        // Calculate Gesture Metrics
                                        var totalDx = 0f
                                        var totalDy = 0f
                                        val swipeThreshold = 100f

                                        for (id in pointerStarts.keys) {
                                            val start = pointerStarts[id] ?: continue
                                            val end = pointerEnds[id] ?: pointerStarts[id]!!
                                            totalDx += (end.x - start.x)
                                            totalDy += (end.y - start.y)
                                        }

                                        val isSwipe =
                                            abs(totalDx) > swipeThreshold || abs(totalDy) > swipeThreshold

                                        if (isSwipe) {
                                            // GESTURE LOGIC
                                            if (abs(totalDx) > abs(totalDy)) {
                                                if (totalDx > 0) { // Right
                                                    if (maxFingers == 1) {
                                                        currentInputConnection?.commitText(" ", 1)
                                                        statusLabel = "Space"
                                                    } else if (maxFingers == 2) {
                                                        currentInputConnection?.commitText("\n", 1)
                                                        statusLabel = "New Line"
                                                    }
                                                } else { // Left
                                                    if (maxFingers == 1) {
                                                        currentInputConnection?.deleteSurroundingText(
                                                            1,
                                                            0
                                                        )
                                                        statusLabel = "Delete"
                                                    } else if (maxFingers == 2) {
                                                        deleteWordBackward()
                                                        statusLabel = "Delete Word"
                                                    }
                                                }
                                            } else {
                                                if (totalDy > 0) { // Down
                                                    if (maxFingers == 2) requestHideSelf(0)
                                                    else if (maxFingers == 3) {
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                            switchToNextInputMethod(false)
                                                        }
                                                    }
                                                } else { // Up
                                                    if (maxFingers == 2) {
                                                        val action =
                                                            currentInputEditorInfo?.imeOptions?.and(
                                                                EditorInfo.IME_MASK_ACTION
                                                            )
                                                        if (action != null && action != EditorInfo.IME_ACTION_NONE) {
                                                            currentInputConnection?.performEditorAction(
                                                                action
                                                            )
                                                        } else {
                                                            currentInputConnection?.commitText(
                                                                "\n",
                                                                1
                                                            )
                                                        }
                                                        statusLabel = "Submit"
                                                    }
                                                }
                                            }
                                        } else {
                                            // Grab EVERY finger touching the screen right now
                                            val currentPointers = currentEvent.changes.filter { it.pressed }

                                            // taps already populated by currentPointers logic above
                                            val rawOutput = runModel(taps, cal.calibratedMain)
                                            val brailleOutput = processRawBraille(rawOutput, dict)

                                            if (brailleOutput.isNotEmpty() && brailleOutput !in listOf(
                                                    "Capital Sign",
                                                    "Caps Lock",
                                                    "Capital Off",
                                                    "Letter Sign",
                                                    "Numeral Sign",
                                                    "Null"
                                                )
                                            ) {
                                                currentInputConnection?.commitText(brailleOutput, 1)
                                            }
                                            statusLabel = "Typed: $brailleOutput"
                                            tapEq = brailleOutput
                                        }

                                        modeLabel = when {
                                            isCapital -> "Caps Lock"
                                            isNumeral -> "Numbers"
                                            else -> "Lowercase"
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Subtle haptic or visual feedback for developers
                            }
                            if (statusLabel == "Ready") {
                                Text(
                                    text = "Braille Keyboard", // Replace with Letter input
                                    color = Color.White,
                                    modifier = Modifier
                                        .semantics {
                                            hideFromAccessibility()
                                        }
                                )
                            }
                            Text(
                                text = tapEq,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 32.dp)
                                    .semantics {
                                        hideFromAccessibility()
                                    }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Please rotate to Landscape for Braille typing",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        return composeView
    }

    private fun deleteWordBackward() {
        val ic = currentInputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(100, 0) ?: ""
        if (textBefore.isEmpty()) return
        var toDelete = 0
        var i = textBefore.length - 1
        while (i >= 0 && textBefore[i].isWhitespace()) { toDelete++; i-- }
        while (i >= 0 && !textBefore[i].isWhitespace()) { toDelete++; i-- }
        if (toDelete > 0) ic.deleteSurroundingText(toDelete, 0)
    }

    fun convertToBraille(cells: List<Boolean>): String{
        // Converting Flags to Braille
        var brailleCell: String = ""
        // Move one to six
        brailleCell += if (cells[0]) "1" else "0"
        brailleCell += if (cells[1]) "1" else "0"
        brailleCell += if (cells[2]) "1" else "0"
        brailleCell += if (cells[3]) "1" else "0"
        brailleCell += if (cells[4]) "1" else "0"
        brailleCell += if (cells[5]) "1" else "0"

        Log.d("CONV", brailleCell)

        return brailleCell
    }

    // This is for Running the Models (k-NN, SVM, Random Forest)
    fun runModel(tapSet: List<Offset>, pointsL2D: List<List<Offset>>): String {
        // Run Model and use Reference Data Points with Actual Data Points
        var cells = mutableListOf(false, false, false, false, false, false)
        val ML_kNN = kNearestNeighbor()
        var set: String?

        // Loop through all Points
        for(i in tapSet){
            set = ML_kNN.kNN(i, pointsL2D)
            when(set){
                "a" -> cells[3] = true
                "b" -> cells[4] = true
                "c" -> cells[5] = true
                "d" -> cells[0] = true
                "e" -> cells[1] = true
                "f" -> cells[2] = true
            }
        }

        return convertToBraille(cells)
    }

    fun processRawBraille(brailleOutput: String, pts: BrailleDictionary): String {
        val output: String = when {
            // Caps Lock Turn Off (checked first if already in caps lock mode)
            brailleOutput == "000001" && isCapital -> {
                isCapital = false
                isCapitalOnce = false
                "Capital Off"
            }
            // Capital Once active -> next press activates Caps Lock
            brailleOutput == "000001" && isCapitalOnce -> {
                isCapital = true
                isCapitalOnce = false
                "Caps Lock"
            }
            // First tap: Capital Once On
            brailleOutput == "000001" -> {
                isCapitalOnce = true
                "Capital Sign"
            }
            brailleOutput == "000011" -> {
                isNumeral = false
                "Letter Sign"
            }
            brailleOutput == "001111" -> {
                isNumeral = true
                "Numeral Sign"
            }
            // Handle numerals if the flag is active
            isNumeral -> when (brailleOutput) {
                "100000" -> "1"
                "110000" -> "2"
                "100100" -> "3"
                "100110" -> "4"
                "100010" -> "5"
                "110100" -> "6"
                "110110" -> "7"
                "110010" -> "8"
                "010100" -> "9"
                "010110" -> "0"
                else -> {
                    val original = pts.brailleConversion[brailleOutput] ?: ""
                    val result = if (isCapital || isCapitalOnce) original.uppercase() else original
                    isCapitalOnce = false
                    result
                }
            }
            // Fallback to standard conversion with capitalization support
            else -> {
                val original = pts.brailleConversion[brailleOutput] ?: ""
                val result = if (isCapital || isCapitalOnce) original.uppercase() else original
                isCapitalOnce = false // consume single capital flag
                val ifNullCheck = if(result != null || result != "") result else "Null"
                ifNullCheck
            }
        }
        return output
    }
}