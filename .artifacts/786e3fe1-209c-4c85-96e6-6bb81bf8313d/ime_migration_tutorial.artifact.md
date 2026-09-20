# Migrating Braille Keyboard to Android IME (Input Method Editor)

This tutorial will guide you through moving your `MockKeyboard` logic into a real Android IME so it can type into any application and work alongside TalkBack.

## 1. Project Setup

### Manifest Configuration
First, you need to declare your IME service in `AndroidManifest.xml`. An IME is a specialized service that the system recognizes as a keyboard.

Add the following to your `<application>` tag:

```xml
<service
    android:name=".BrailleIME"
    android:label="Braillify Keyboard"
    android:permission="android.permission.BIND_INPUT_METHOD"
    android:exported="true">
    <intent-filter>
        <action android:name="android.view.InputMethod" />
    </intent-filter>
    <meta-data
        android:name="android.view.im"
        android:resource="@xml/method" />
</service>
```

### IME Metadata
Create a new file at `app/src/main/res/xml/method.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<input-method xmlns:android="http://schemas.android.com/apk/res/android"
    android:supportsSwitchingToNextInputMethod="true" />
```

## 2. Create the IME Service

Create a new file `BrailleIME.kt`. This service will handle the connection between your UI and the text field.

```kotlin
package com.example.braillify

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class BrailleIME : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            // Necessary for Jetpack Compose to work in a Service
            setViewTreeLifecycleOwner(this@BrailleIME)
            setViewTreeViewModelStoreOwner(this@BrailleIME)
            setViewTreeSavedStateRegistryOwner(this@BrailleIME)

            setContent {
                val keyboard = MockKeyboard()
                // We pass a callback to the Composable to handle typing
                keyboard.BrailleSandbox(onCommitText = { text ->
                    val ic = currentInputConnection
                    ic?.commitText(text, 1)
                })
            }
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}
```

## 3. Adapt `MockKeyboard.kt`

You need to modify your `BrailleSandbox` to accept an `onCommitText` callback. Also, for TalkBack to work correctly, you should add accessibility announcements.

### Update `MockKeyboard.kt` logic:

```kotlin
@Composable
fun BrailleSandbox(onCommitText: (String) -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current

    // ... existing remember states ...

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    // 1. Capture chord
                    val downEvent = awaitFirstDown()
                    // ... capture logic ...

                    // 2. Process logic
                    val rawOutput = runModel(taps, dict.pointsL2D)
                    val brailleOutput = processRawBraille(rawOutput, points)

                    // 3. Commit and Announce
                    if (brailleOutput.isNotEmpty()) {
                        // If it's a character, type it
                        if (brailleOutput.length == 1) {
                            onCommitText(brailleOutput)
                        }

                        // Always announce to TalkBack
                        view.announceForAccessibility(brailleOutput)
                    }
                    taps.clear()
                }
            }
    ) {
        // ... UI ...
    }
}
```

## 4. TalkBack & Accessibility Integration

The biggest challenge with a Braille IME is the conflict between **multi-touch chords** and **TalkBack's "Explore by Touch"**. By default, TalkBack intercepts all single touches to "explore" the screen, which breaks multi-finger chords.

### Bypassing "Explore by Touch"

To allow raw multi-touch input while TalkBack is active, follow these strategies:

#### A. The "No Hide Descendants" Strategy
Set your keyboard container to hide its internal "dots" from the accessibility tree. This stops TalkBack from focusing on individual dots and instead treats the whole keyboard as one unit.

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .semantics {
            // Tells TalkBack to treat this as a single functional unit
            contentDescription = "Braille Keyboard"
        }
        .pointerInput(Unit) { ... }
)
```

#### B. Accessibility Pass-through
Explain to your users that they can use the **Double-tap and Hold** gesture. This is a system-wide gesture that tells TalkBack to pass all subsequent touch events directly to the app until the fingers are lifted. This is the only way to get raw multi-touch in a standard IME without extra permissions.

#### C. Implementing "TalkBack Mode" (Sequential Input)
When you detect that TalkBack is active (using `AccessibilityManager`), you can switch to a **Sequential Input** mode:
1. User taps Dot 1 (TalkBack announces "Dot 1 selected").
2. User taps Dot 4 (TalkBack announces "Dot 4 selected").
3. User performs a "Commit" gesture (like a swipe right) to type the resulting character.

#### D. Custom Accessibility Actions
Expose your Braille chords as actions in the TalkBack "Actions" menu.

```kotlin
val deleteAction = AccessibilityAction("Delete Word") {
    handleDelete()
    true
}
// Add to Modifier.semantics { customActions = listOf(deleteAction) }
```

> [!IMPORTANT]
> **Professional Braille Keyboards**: The built-in Google Braille Keyboard works because it is part of an **Accessibility Service**, not just an IME. Accessibility Services have the power to request `FLAG_REQUEST_TOUCH_EXPLORATION_MODE`, which lets them handle all touch events directly. If you want a seamless experience, you may eventually need to implement an `AccessibilityService` alongside your IME.

## 5. Testing your IME
1.  **Deploy**: Build and run your app.
2.  **Enable**: Go to **Settings > System > Languages & input > On-screen keyboard > Manage on-screen keyboards** and toggle "Braillify Keyboard" ON.
3.  **Switch**: Open any text field (like Search) and switch to your keyboard using the "Globe" icon or the keyboard notification.

---
**Next Steps**:
- Implement **Backspace** (swipe gesture).
- Implement **Space** (swipe or specific chord).
- Add visual feedback that TalkBack can "see" if necessary.
