package com.example.braillify.screens

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Profile(
    val id: String,
    val name: String,
    val portraitFileName: String,
    val landscapeFileName: String
)

object ProfileManager {
    private const val PREFS_NAME = "braillify_profiles_prefs"
    private const val KEY_PROFILES_JSON = "profiles_list_json"
    private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"

    const val DEFAULT_PROFILE_ID = "profile_1"

    fun getProfiles(context: Context): List<Profile> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_PROFILES_JSON, null)
        val defaultList = listOf(
            Profile(
                id = DEFAULT_PROFILE_ID,
                name = "Profile 1",
                portraitFileName = "calibrated_data_portrait.json",
                landscapeFileName = "calibrated_data_landscape.json"
            )
        )

        if (jsonString.isNullOrEmpty()) {
            saveProfiles(context, defaultList)
            return defaultList
        }

        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Profile>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val name = obj.optString("name", "Profile ${i + 1}")

                // Force profile-specific unique filenames for non-default profiles to prevent sharing the same file
                val portrait = if (id == DEFAULT_PROFILE_ID) {
                    obj.optString("portraitFileName", "calibrated_data_portrait.json")
                } else {
                    "calibrated_data_${id}_portrait.json"
                }
                val landscape = if (id == DEFAULT_PROFILE_ID) {
                    obj.optString("landscapeFileName", "calibrated_data_landscape.json")
                } else {
                    "calibrated_data_${id}_landscape.json"
                }

                list.add(
                    Profile(
                        id = id,
                        name = name,
                        portraitFileName = portrait,
                        landscapeFileName = landscape
                    )
                )
            }
            if (list.isEmpty()) {
                saveProfiles(context, defaultList)
                defaultList
            } else {
                saveProfiles(context, list)
                list
            }
        } catch (e: Exception) {
            saveProfiles(context, defaultList)
            defaultList
        }
    }

    fun saveProfiles(context: Context, profiles: List<Profile>) {
        val jsonArray = JSONArray()
        for (p in profiles) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("portraitFileName", p.portraitFileName)
                put("landscapeFileName", p.landscapeFileName)
            }
            jsonArray.put(obj)
        }
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(KEY_PROFILES_JSON, jsonArray.toString())
        editor.commit()
    }

    fun getActiveProfileId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_PROFILE_ID, DEFAULT_PROFILE_ID) ?: DEFAULT_PROFILE_ID
    }

    fun setActiveProfileId(context: Context, profileId: String) {
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(KEY_ACTIVE_PROFILE_ID, profileId)
        editor.commit()
        Log.d("ProfileManager", "Active profile set to: $profileId")
    }

    fun getActiveProfile(context: Context): Profile {
        val activeId = getActiveProfileId(context)
        val profiles = getProfiles(context)
        return profiles.find { it.id == activeId } ?: profiles.firstOrNull() ?: Profile(
            id = DEFAULT_PROFILE_ID,
            name = "Profile 1",
            portraitFileName = "calibrated_data_portrait.json",
            landscapeFileName = "calibrated_data_landscape.json"
        )
    }

    fun getProfileById(context: Context, profileId: String): Profile? {
        return getProfiles(context).find { it.id == profileId }
    }

    fun addProfile(context: Context, name: String): Profile {
        val profiles = getProfiles(context).toMutableList()
        val nextIndex = System.currentTimeMillis()
        val id = "profile_$nextIndex"
        val portraitFileName = "calibrated_data_${id}_portrait.json"
        val landscapeFileName = "calibrated_data_${id}_landscape.json"
        val newProfile = Profile(
            id = id,
            name = name.ifBlank { "Profile ${profiles.size + 1}" },
            portraitFileName = portraitFileName,
            landscapeFileName = landscapeFileName
        )
        profiles.add(newProfile)
        saveProfiles(context, profiles)
        Log.d("ProfileManager", "Added new profile: ${newProfile.name} ($id)")
        return newProfile
    }

    fun deleteProfile(context: Context, profileId: String) {
        val profiles = getProfiles(context).toMutableList()
        val target = profiles.find { it.id == profileId } ?: return
        if (profiles.size <= 1) return // Do not delete the last remaining profile

        try {
            val pFile = File(context.filesDir, target.portraitFileName)
            if (pFile.exists()) pFile.delete()
            val lFile = File(context.filesDir, target.landscapeFileName)
            if (lFile.exists()) lFile.delete()
        } catch (e: Exception) {
            Log.e("ProfileManager", "Error deleting profile files", e)
        }

        profiles.remove(target)
        saveProfiles(context, profiles)
        if (getActiveProfileId(context) == profileId) {
            setActiveProfileId(context, profiles.first().id)
        }
    }
}
