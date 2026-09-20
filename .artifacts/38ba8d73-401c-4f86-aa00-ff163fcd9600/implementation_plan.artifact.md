# Implementation Plan: Convert MockKeyboard to functional IME

This plan outlines the steps to integrate the Braille input logic from `MockKeyboard` into the existing `Keyboard` service to create a functional Android Input Method Editor.

## User Review Required

> [!IMPORTANT]
> The current k-NN logic in `MockKeyboard` seems to swap left and right dots (e.g., Dot 1/Left-Top maps to `cells[3]`). I will preserve this logic for now as it matches your current implementation, but we may need to adjust it if the output characters don't match your expectations.

## Proposed Changes

### Logic & UI Integration

#### [MODIFY] [Keyboard.kt](file:///C:/Users/Gabrielle/AndroidStudioProjects/Braillify/app/src/main/java/com/example/braillify/Keyboard.kt)
- Integrate the Braille processing logic (k-NN, capitalization flags, numeral flags).
- Update `onCreateInputView` to use the `BrailleSandbox` UI.
- Use `currentInputConnection` to commit text when a character is recognized.
- Add support for common IME actions like backspace (via a specific gesture or chord, or I'll add a simple detection for it).

#### [MODIFY] [MockKeyboard.kt](file:///C:/Users/Gabrielle/AndroidStudioProjects/Braillify/app/src/main/java/com/example/braillify/MockKeyboard.kt)
- Extract the core composable and logic so it can be reused or simply moved to `Keyboard.kt`.
- Make the state (flags) manageable by the service.

## Verification Plan

### Automated Tests
- I will verify that the project still builds after the changes.

### Manual Verification
1.  Deploy the app to the device.
2.  Enable the "Braillify" keyboard in Android Settings.
3.  Open any text field (e.g., Search or a Note app).
4.  Switch to the Braillify keyboard.
5.  Perform chords (multi-finger taps) to type Braille characters and verify they appear in the text field.
