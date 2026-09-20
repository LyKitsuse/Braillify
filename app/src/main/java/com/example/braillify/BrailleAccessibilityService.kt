package com.example.braillify

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

import android.view.accessibility.AccessibilityManager

class BrailleAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if needed
        Log.d("BrailleAccessibility", "Event: ${event?.eventType}")
    }

    override fun onInterrupt() {
        Log.d("BrailleAccessibility", "Service Interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BrailleAccessibility", "Service Connected")
        
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        
        // Listen for changes in Touch Exploration (TalkBack)
        am.addTouchExplorationStateChangeListener { enabled ->
            Log.d("BrailleAccessibility", "TalkBack/TouchExploration changed: $enabled")
        }
        
        val isTouchExplorationEnabled = am.isTouchExplorationEnabled
        Log.d("BrailleAccessibility", "Initial Touch Exploration: $isTouchExplorationEnabled")
    }
}
