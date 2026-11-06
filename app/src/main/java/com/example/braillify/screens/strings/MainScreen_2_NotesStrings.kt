package com.example.braillify.screens

import android.content.Context

object NoteStrings {
    // English Translation
    val english = mapOf(
        // Notes Screen
        "notes_title" to "Notes",
        "notes_add" to "Add Note",
        "notes_empty" to "No notes yet. Tap \"Add Note\" to get started.",
        "notes_dialog_desc" to "What would you like to do with this note?",
        "notes_edit" to "Edit",
        "notes_delete" to "Delete",

        // Note Editor
        "note_new_title" to "New Note",
        "note_edit_title" to "Edit Note",
        "note_title_label" to "Title",
        "note_body_label" to "Write your note...",
        "note_save" to "Save",
        "note_untitled" to "Untitled",
        "note_no_preview" to "No preview"
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Notes Screen
        "notes_title" to "Mga Note",
        "notes_add" to "Magdagdag ng Note",
        "notes_empty" to "Wala pang mga note. I-tap ang \"Magdagdag ng Note\" upang magsimula.",
        "notes_dialog_desc" to "Ano ang gusto mong gawin sa note na ito?",
        "notes_edit" to "I-edit",
        "notes_delete" to "Burahin",

        // Note Editor
        "note_new_title" to "Bagong Note",
        "note_edit_title" to "I-edit ang Note",
        "note_title_label" to "Pamagat",
        "note_body_label" to "Isulat ang iyong note...",
        "note_save" to "I-save",
        "note_untitled" to "Walang Pamagat",
        "note_no_preview" to "Walang preview"
    )

    fun get(context: Context, key: String): String {
        val lang = SettingsPrefs.getLanguage(context)
        return if (lang == "Tagalog") {
            tagalog[key] ?: english[key] ?: key
        } else {
            english[key] ?: key
        }
    }
}