package com.example.braillify

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.machineLearningModels.kNearestNeighbor
import com.example.braillify.machineLearningModels.randomForest
import com.example.braillify.machineLearningModels.svm
import com.example.braillify.ui.theme.BraillifyTheme
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
import com.example.braillify.screens.SettingsPrefs
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
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

    private val forest = randomForest()
    private val mL_kNN1 = kNearestNeighbor()
    private val mL_kNN2 = svm()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    companion object {
        var isCalibrating by mutableStateOf(false)
        var calibrationStep by mutableIntStateOf(0)

        // Array of Words the TTS should Say during Calibration
        val calibrationPrompts = listOf(
            "Input Dot 1", "Input Dot 2", "Input Dot 3", "Input Dot 4", "Input Dot 5", "Input Dot 6",
            "Input a", "Input b", "Input c", "Input d", "Input e", "Input f", "Input g",
            "Input h", "Input i", "Input j", "Input k", "Input l", "Input m", "Input n",
            "Input o", "Input p", "Input q", "Input r", "Input s", "Input t", "Input u",
            "Input v", "Input w", "Input x", "Input y", "Input z",
            "Input number sign",
            "Input 1", "Input 2", "Input 3", "Input 4", "Input 5",
            "Input 6", "Input 7", "Input 8", "Input 9", "Input 0"
        )
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        initTts()
    }

    private fun initTts() {
        if (tts == null) {
            tts = TextToSpeech(applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isTtsInitialized = true
                    if (isCalibrating) {
                        speakCalibrationStep()
                    }
                }
            }
        }
    }

    fun speakText(text: String) {
        if (isTtsInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KeyboardTTS")
        }
    }

    fun speakCharacter(output: String) {
        if (output.isEmpty() || output == "Null") return
        val textToSpeak = when (output) {
            "," -> "Comma"
            ";" -> "Semicolon"
            ":" -> "Colon"
            "." -> "Period"
            "!" -> "Exclamation mark"
            "?" -> "Question mark"
            "'" -> "Apostrophe"
            "-" -> "Hyphen"
            "(" -> "Open parenthesis"
            ")" -> "Close parenthesis"
            " " -> "Space"
            "\n" -> "New Line"
            else -> output
        }
        speakText(textToSpeak)
    }

    fun speakCalibrationStep() {
        if (calibrationStep in calibrationPrompts.indices) {
            speakText(calibrationPrompts[calibrationStep])
        }
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        initTts()
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val cal = Calibrate()
        cal.pullCalibratedData(this, isLandscape)
        if (isCalibrating) {
            speakCalibrationStep()
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        if (isCalibrating) {
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val cal = Calibrate()
            cal.exitCalibration(this, isLandscape)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isCalibrating) {
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val cal = Calibrate()
            cal.exitCalibration(this, isLandscape)
        }
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsInitialized = false
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return true
    }

    override fun onCreateInputView(): View {
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
                    info.isVisibleToUser = false
                    info.isFocusable = false
                    info.isImportantForAccessibility = false
                    info.className = View::class.java.name
                    info.packageName = packageName
                }

                override fun dispatchPopulateAccessibilityEvent(host: View, event: AccessibilityEvent): Boolean {
                    return true
                }
            }

            setOnHoverListener { _, _ -> true }

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
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                var statusLabel by remember { mutableStateOf("Ready") }
                var modeLabel by remember { mutableStateOf("Lowercase") }
                val taps = remember { mutableStateListOf<Offset>() }
                var tapEq by remember { mutableStateOf("Tap Anywhere!") }
                var brailleOutput by remember { mutableStateOf("") }

                val cal = remember { Calibrate() }
                cal.pullCalibratedData(this@Keyboard, isLandscape)

                var testIncrement by remember { mutableStateOf(0) }
                var printTapPos by remember { mutableStateOf("") }

                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x804B0082))
                            .clearAndSetSemantics { }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        val initialDown = awaitFirstDown()
                                        val dict = BrailleDictionary

                                        val chordPointers = mutableMapOf<Long, Offset>()
                                        chordPointers[initialDown.id.value] = initialDown.position

                                        withTimeoutOrNull(50L) {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                for (change in event.changes) {
                                                    if (change.pressed) {
                                                        chordPointers[change.id.value] = change.position
                                                    }
                                                }
                                            }
                                        }

                                        taps.clear()
                                        taps.addAll(chordPointers.values)
                                        for (tap in taps) {
                                            Log.d("TAP", "Tap at: X=${tap.x}, Y=${tap.y}")
                                        }

                                        vibratePhone()

                                        val pointerStarts = mutableMapOf<Long, Offset>()
                                        val pointerEnds = mutableMapOf<Long, Offset>()

                                        for (id in chordPointers.keys) {
                                            pointerStarts[id] = chordPointers[id]!!
                                            pointerEnds[id] = chordPointers[id]!!
                                        }

                                        var maxFingers = pointerStarts.size

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val currentCount = event.changes.count { it.pressed }
                                            if (currentCount > maxFingers) maxFingers = currentCount

                                            for (change in event.changes) {
                                                val id = change.id.value
                                                if (change.pressed) {
                                                    if (!pointerStarts.containsKey(id)) pointerStarts[id] = change.position
                                                    pointerEnds[id] = change.position
                                                }
                                            }
                                            if (event.changes.all { !it.pressed }) break
                                        }

                                        var totalDx = 0f
                                        var totalDy = 0f
                                        val swipeThreshold = 100f

                                        for (id in pointerStarts.keys) {
                                            val start = pointerStarts[id] ?: continue
                                            val end = pointerEnds[id] ?: pointerStarts[id]!!
                                            totalDx += (end.x - start.x)
                                            totalDy += (end.y - start.y)
                                        }

                                        val isSwipe = abs(totalDx) > swipeThreshold || abs(totalDy) > swipeThreshold

                                        // If Swipe, otherwise Tap
                                        if (isSwipe) {
                                            swiping(
                                                totalDx = totalDx,
                                                totalDy = totalDy,
                                                maxFingers = maxFingers,
                                                cal = cal,
                                                statusLabelSetter = { statusLabel = it },
                                                printTapPosSetter = { printTapPos = it }
                                            )
                                        } else {
                                            // If Calibrating
                                            if (isCalibrating) {
                                                calibrationProcess(
                                                    taps = taps,
                                                    cal = cal,
                                                    statusLabelSetter = { statusLabel = it },
                                                    printTapPosSetter = { printTapPos = it }
                                                )
                                            } else if (taps.isNotEmpty()) {
                                                // If the Input is a dot point
                                                val rawOutput = runModel(taps, cal.calibratedMain)
                                                brailleOutput = processRawBraille(rawOutput, dict)

                                                if (brailleOutput.isNotEmpty() && brailleOutput != "Null") {
                                                    if (brailleOutput !in listOf(
                                                            "Capital Sign", "Caps Lock", "Capital Off",
                                                            "Letter Sign", "Numeral Sign"
                                                        )
                                                    ) {
                                                        currentInputConnection?.commitText(brailleOutput, 1)
                                                    }
                                                    speakCharacter(brailleOutput)
                                                }
                                                statusLabel = "Typed: $brailleOutput"
                                                tapEq = brailleOutput
                                            }
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
                                // Draw a purple circle for every tap
                                for (tap in taps) {
                                    drawCircle(
                                        color = Color(0xFF6200EE),
                                        radius = 10f,
                                        center = tap
                                    )
                                }
                            }
                            val overlayText = if (isCalibrating) {
                                val currentPrompt = if (calibrationStep in calibrationPrompts.indices) {
                                    calibrationPrompts[calibrationStep]
                                } else "Calibration Complete!"
                                "CALIBRATION MODE (${calibrationStep + 1}/${calibrationPrompts.size})\n$currentPrompt\n(Tap screen when ready)"
                            } else if (brailleOutput.isEmpty()) {
                                "Tap Anywhere!"
                            } else {
                                brailleOutput
                            }
                            Text(
                                text = overlayText,
                                color = if (isCalibrating) Color.Yellow else Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                                    .semantics {
                                        hideFromAccessibility()
                                    }
                            )
                        }
                    }
                }
            }
        }
        return composeView
    }

    fun calibrationProcess(
        taps: List<Offset>,
        cal: Calibrate,
        statusLabelSetter: (String) -> Unit,
        printTapPosSetter: (String) -> Unit
    ){
        if (taps.isNotEmpty()) {
            // Calibrates the first inputs
            if (calibrationStep <= 5) {
                cal.recordInitialDot(calibrationStep, taps.first())
                calibrationStep++
                if (calibrationStep < calibrationPrompts.size) {
                    speakCalibrationStep()
                    statusLabelSetter("Calibration: ${calibrationPrompts[calibrationStep]}")
                }
                // Calibrates per letter
            } else if (calibrationStep in 6 until calibrationPrompts.size) {
                for (p in taps) {
                    cal.appendCalibratedPoint(p)
                }
                calibrationStep++
                if (calibrationStep < calibrationPrompts.size) {
                    speakCalibrationStep()
                    statusLabelSetter("Calibration: ${calibrationPrompts[calibrationStep]}")
                } else {
                    val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    cal.saveCalibratedData(this@Keyboard, cal.calibratedMain, isLandscape)
                    isCalibrating = false
                    calibrationStep = 0
                    speakText("Calibration complete")
                    statusLabelSetter("Calibration Complete!")
                    printTapPosSetter("Calibration Complete!\nTap Anywhere to Type")
                }
            }
        }
    }
    fun swiping(totalDx: Float,totalDy: Float,maxFingers: Int,cal: Calibrate,statusLabelSetter: (String) -> Unit,printTapPosSetter: (String) -> Unit){
        if (isCalibrating) {
            cal.exitCalibration(this@Keyboard, resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            speakText("Calibration cancelled")
            statusLabelSetter("Ready")
            printTapPosSetter("")
            if (totalDy > 0 && maxFingers >= 2) {
                requestHideSelf(0)
            }
        } else if (abs(totalDx) > abs(totalDy)) {
            if (totalDx > 0) {
                if (maxFingers == 1) {
                    currentInputConnection?.commitText(" ", 1)
                    statusLabelSetter("Space")
                    speakText("Space")
                } else if (maxFingers == 2) {
                    currentInputConnection?.commitText("\n", 1)
                    statusLabelSetter("New Line")
                    speakText("New Line")
                }
            } else {
                if (maxFingers == 1) {
                    currentInputConnection?.deleteSurroundingText(1, 0)
                    statusLabelSetter("Delete")
                    speakText("Delete")
                } else if (maxFingers == 2) {
                    deleteWordBackward()
                    statusLabelSetter("Delete Word")
                    speakText("Delete Word")
                }
            }
        } else {
            if (totalDy > 0) {
                if (maxFingers == 2) requestHideSelf(0)
                else if (maxFingers == 3) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        switchToNextInputMethod(false)
                    }
                }
            } else {
                if (maxFingers == 2) {
                    val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
                    if (action != null && action != EditorInfo.IME_ACTION_NONE) {
                        currentInputConnection?.performEditorAction(action)
                    } else {
                        currentInputConnection?.commitText("\n", 1)
                    }
                    statusLabelSetter("Submit")
                    speakText("Submit")
                }
            }
        }
    }

    fun vibratePhone(){
        if(SettingsPrefs.getHapticOn(this)){
            // Vibrates Phone
            val vibrator = this@Keyboard.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }

    fun deleteWordBackward() {
        val ic = currentInputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(100, 0) ?: ""
        if (textBefore.isEmpty()) return
        var toDelete = 0
        var i = textBefore.length - 1
        while (i >= 0 && textBefore[i].isWhitespace()) { toDelete++; i-- }
        while (i >= 0 && !textBefore[i].isWhitespace()) { toDelete++; i-- }
        if (toDelete > 0) ic.deleteSurroundingText(toDelete, 0)
    }

    fun convertToBraille(cells: List<Boolean>): String {
        var brailleCell: String = ""
        brailleCell += if (cells[0]) "1" else "0"
        brailleCell += if (cells[1]) "1" else "0"
        brailleCell += if (cells[2]) "1" else "0"
        brailleCell += if (cells[3]) "1" else "0"
        brailleCell += if (cells[4]) "1" else "0"
        brailleCell += if (cells[5]) "1" else "0"

        Log.d("CONV", brailleCell)

        return brailleCell
    }

    fun runModel(tapSet: List<Offset>, pointsL2D: List<List<Offset>>): String {
        var cells = mutableListOf(false, false, false, false, false, false)

        var set: String?

        for (i in tapSet) {
//            set  = forest.randomForestAlgo(i, pointsL2D)
            set = mL_kNN1.kNN(i, pointsL2D)
            when (set) {
                "a" -> cells[0] = true
                "b" -> cells[1] = true
                "c" -> cells[2] = true
                "d" -> cells[3] = true
                "e" -> cells[4] = true
                "f" -> cells[5] = true
            }
        }

        return convertToBraille(cells)
    }

    fun processRawBraille(brailleOutput: String, pts: BrailleDictionary): String {
        val output: String = when {
            brailleOutput == "000001" && isCapital -> {
                isCapital = false
                isCapitalOnce = false
                "Capital Off"
            }
            brailleOutput == "000001" && isCapitalOnce -> {
                isCapital = true
                isCapitalOnce = false
                "Caps Lock"
            }
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
                // if not a number then print the Character
                else -> {
                    val knownChar = pts.brailleConversion[brailleOutput]
                    val result = if (knownChar != null) {
                        if (isCapital || isCapitalOnce) knownChar.uppercase() else knownChar
                    } else {
                        formatDots(brailleOutput)
                    }
                    isCapitalOnce = false
                    result
                }
            }
            // print the Character
            else -> {
                val knownChar = pts.brailleConversion[brailleOutput]
                val result = if (knownChar != null) {
                    if (isCapital || isCapitalOnce) knownChar.uppercase() else knownChar
                } else {
                    formatDots(brailleOutput)
                }
                isCapitalOnce = false
                val ifNullCheck = if (result.isNotEmpty()) result else "Null"
                ifNullCheck
            }
        }
        return output
    }

    fun formatDots(brailleOutput: String): String {
        val activeDots = brailleOutput.mapIndexedNotNull { index, char ->
            if (char == '1') index + 1 else null
        }
        return if (activeDots.isNotEmpty()) {
            "dots ${activeDots.joinToString(" ")}"
        } else {
            ""
        }
    }
}