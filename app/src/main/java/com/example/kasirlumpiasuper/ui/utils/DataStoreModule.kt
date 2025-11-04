package com.example.kasirlumpiasuper.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore by preferencesDataStore("user_prefs")

object DataStoreKeys {
    val User_UID = stringPreferencesKey("user_uid")
    val User_ROLE = stringPreferencesKey("user_role")      // ⬅️ baru
    val User_NAME = stringPreferencesKey("user_name")      // ⬅️ opsional
}