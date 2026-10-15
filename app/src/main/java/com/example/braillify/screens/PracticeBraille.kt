package com.example.braillify.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.braillify.R

private val Purple = Color(0xFF5B3FE4)
private val PurpleLight = Color(0xFFE8E3FB)
private val Background = Color(0xFFF2F0F5)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)

@Composable
fun PracticeBrailleScreen(onBack: () -> Unit = {}) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    // Hide keyboard
    BackHandler {
        hideKeyboard(context)
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Hidden
        Box(
            modifier = Modifier
                .size(1.dp)
                .graphicsLayer { alpha = 0.01f }
                .semantics { hideFromAccessibility() }
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.focusRequester(focusRequester)
            )
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "‹", color = TextDark, fontSize = 28.sp)
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Practice Braille",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            Spacer(Modifier.size(32.dp))
        }

        Spacer(Modifier.height(24.dp))

        PracticeRow(
            iconRes = R.drawable.pb_braille_keyboard_24px,
            title = "Braille Keyboard",
            subtitle = "Type using braille and see the result.",
            onClick = {
                Log.d("DEBUG", "Braille Keyboard tapped")
                focusRequester.requestFocus()
                // Small delay
                Handler(Looper.getMainLooper()).postDelayed({
                    showKeyboard(context)
                }, 100)
            }
        )

        Spacer(Modifier.height(16.dp))

        PracticeRow(
            iconRes = R.drawable.pb_braille_dictionary_24px,
            title = "Braille Dictionary",
            subtitle = "Browse and learn braille characters, numbers and punctuations.",
            onClick = { Log.d("DEBUG", "Braille Dictionary tapped") }
        )
    }
}

@Composable
private fun PracticeRow(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(PurpleLight, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.size(16.dp))

            // Title + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(Modifier.size(8.dp))

            // Chevron
            Text(text = "›", color = TextGray, fontSize = 22.sp)
        }
    }
}

private fun showKeyboard(context: Context) {
    val activity = context.findActivity() ?: return
    val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    controller.show(WindowInsetsCompat.Type.ime())
    controller.show(WindowInsetsCompat.Type.systemBars())
}

private fun hideKeyboard(context: Context) {
    val activity = context.findActivity() ?: return
    val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    controller.hide(WindowInsetsCompat.Type.ime())
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun PracticeBraillePreview() {
    PracticeBrailleScreen()
}