package com.example.kasirlumpiasuper.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_LAST_BUSINESS_DATE = stringPreferencesKey("last_business_date")
    }

    val lastBusinessDateFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_BUSINESS_DATE]
    }

    suspend fun saveLastBusinessDate(date: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_BUSINESS_DATE] = date
        }
    }

    suspend fun getLastBusinessDate(): String? {
        return context.dataStore.data.first()[KEY_LAST_BUSINESS_DATE]
    }
}