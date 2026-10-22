package com.example.braillify.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Background = Color(0xFFF2F0F5)
private val RowBackground = Color(0xFFEDEBF2)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)
private val Purple = Color(0xFF5B3FE4)

data class NoteItem(
    val title: String,
    val date: String,
    val preview: String,
    val body: String = ""
)

// Storage
private const val NOTES_PREFS = "braillify_notes"
private const val NOTES_KEY = "saved_notes"

internal fun loadNotes(context: Context): List<NoteItem> {
    val raw = context.getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE)
        .getString(NOTES_KEY, null) ?: return emptyList()
    return try {
        val arr = JSONArray(raw)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            NoteItem(
                title = o.getString("title"),
                date = o.getString("date"),
                preview = o.getString("preview"),
                body = o.getString("body")
            )
        }
    } catch (_: Throwable) {
        emptyList()
    }
}

internal fun saveNotes(context: Context, notes: List<NoteItem>) {
    val arr = JSONArray()
    notes.forEach { n ->
        arr.put(JSONObject().apply {
            put("title", n.title)
            put("date", n.date)
            put("preview", n.preview)
            put("body", n.body)
        })
    }
    context.getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE)
        .edit().putString(NOTES_KEY, arr.toString()).apply()
}

internal fun notesCount(context: Context): Int = loadNotes(context).size

// Screen
@Composable
fun NotesScreen(
    onBack: () -> Unit = {},
    onNotesChanged: () -> Unit = {}   // notify MainScreen to refresh count
) {
    val context = LocalContext.current
    val notes = remember {
        mutableStateListOf<NoteItem>().apply { addAll(loadNotes(context)) }
    }
    var showEditor by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var dialogNoteIndex by remember { mutableStateOf<Int?>(null) }

    BackHandler(enabled = true) {
        if (showEditor) showEditor = false else onBack()
    }

    // Editor takes over
    if (showEditor) {
        val editIndex = editingIndex
        NoteEditorScreen(
            initial = editIndex?.let { notes[it] },
            onBack = { showEditor = false; editingIndex = null },
            onSave = { newNote ->
                if (editIndex != null) {
                    notes[editIndex] = newNote
                } else {
                    notes.add(0, newNote)
                }
                saveNotes(context, notes)
                onNotesChanged()
                showEditor = false
                editingIndex = null
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
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
                text = "Notes",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    editingIndex = null
                    showEditor = true
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text("Add Note", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet. Tap \"Add Note\" to get started.",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes) { note ->
                    val index = notes.indexOf(note)
                    NoteCard(
                        note = note,
                        onClick = { dialogNoteIndex = index }
                    )
                }
            }
        }
    }

    // Delete / Edit dialog
    dialogNoteIndex?.let { index ->
        val note = notes.getOrNull(index) ?: return@let
        AlertDialog(
            onDismissRequest = { dialogNoteIndex = null },
            title = {
                Text(text = note.title, color = TextDark, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "What would you like to do with this note?", color = TextGray)
            },
            confirmButton = {
                TextButton(onClick = {
                    editingIndex = index
                    showEditor = true
                    dialogNoteIndex = null
                }) {
                    Text("Edit", color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    notes.removeAt(index)
                    saveNotes(context, notes)
                    onNotesChanged()
                    dialogNoteIndex = null
                }) {
                    Text("Delete", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun NoteCard(note: NoteItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RowBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, color = TextDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(text = note.date, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Text(text = note.preview, color = TextGray, fontSize = 13.sp, maxLines = 2)
            }
            Text(text = "›", color = TextGray, fontSize = 22.sp)
        }
    }
}

// Editor
@Composable
private fun NoteEditorScreen(
    initial: NoteItem?,
    onBack: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var body by remember { mutableStateOf(initial?.body ?: "") }

    BackHandler(enabled = true) { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
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
                text = if (initial == null) "New Note" else "Edit Note",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(32.dp))
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title", color = TextGray) },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = TextDark,
                unfocusedLabelColor = TextGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Write your note...", color = TextGray) },
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = TextDark,
                unfocusedLabelColor = TextGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isBlank() && body.isBlank()) return@Button
                val now = if (initial != null) initial.date
                else SimpleDateFormat("MMMM d, yyyy · h:mm a", Locale.US).format(Date())
                val preview = body.replace("\n", " ").take(60) +
                        if (body.length > 60) "..." else ""
                onSave(
                    NoteItem(
                        title = title.ifBlank { "Untitled" },
                        date = now,
                        preview = preview.ifBlank { "No preview" },
                        body = body
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text("Save", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotesPreview() {
    NotesScreen()
}