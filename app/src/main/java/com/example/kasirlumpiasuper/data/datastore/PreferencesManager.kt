package com.example.kasirlumpiasuper.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        private val KEY_MANUAL_LOCK_ACTIVE = booleanPreferencesKey("manual_lock_active")
        private val KEY_LOCKED_DATE = stringPreferencesKey("locked_date")
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

    suspend fun saveManualLock(isLocked: Boolean, lockedDate: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MANUAL_LOCK_ACTIVE] = isLocked
            if (isLocked && lockedDate != null) {
                prefs[KEY_LOCKED_DATE] = lockedDate
            } else {
                prefs.remove(KEY_LOCKED_DATE)
            }
        }
    }

    suspend fun isManualLockActive(): Boolean {
        return context.dataStore.data.first()[KEY_MANUAL_LOCK_ACTIVE] ?: false
    }

    suspend fun getLockedDate(): String? {
        return context.dataStore.data.first()[KEY_LOCKED_DATE]
    }

    suspend fun clearManualLock() {
        context.dataStore.edit { prefs ->
            prefs[KEY_MANUAL_LOCK_ACTIVE] = false
            prefs.remove(KEY_LOCKED_DATE)
        }
    }
}