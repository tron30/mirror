package com.mirrorx.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mirrorx_settings",
)

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<MirrorSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            MirrorSettings(
                defaultZoomLevel = preferences[Keys.DefaultZoom]?.coerceIn(1f, 4f) ?: 1f,
                autoHideControlsMillis = preferences[Keys.AutoHideMillis]?.coerceIn(1_000L, 5_000L)
                    ?: 2_000L,
                startBrightnessBoost = preferences[Keys.StartBrightnessBoost] ?: false,
                startMirrored = preferences[Keys.StartMirrored] ?: true,
                theme = preferences[Keys.Theme]?.let(::decodeTheme) ?: AppTheme.System,
            )
        }

    suspend fun setDefaultZoomLevel(value: Float) {
        dataStore.edit { it[Keys.DefaultZoom] = value.coerceIn(1f, 4f) }
    }

    suspend fun setAutoHideControlsMillis(value: Long) {
        dataStore.edit { it[Keys.AutoHideMillis] = value.coerceIn(1_000L, 5_000L) }
    }

    suspend fun setStartBrightnessBoost(enabled: Boolean) {
        dataStore.edit { it[Keys.StartBrightnessBoost] = enabled }
    }

    suspend fun setStartMirrored(enabled: Boolean) {
        dataStore.edit { it[Keys.StartMirrored] = enabled }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.Theme] = theme.name }
    }

    private fun decodeTheme(value: String): AppTheme =
        AppTheme.entries.firstOrNull { it.name == value } ?: AppTheme.System

    private object Keys {
        val DefaultZoom = floatPreferencesKey("default_zoom")
        val AutoHideMillis = longPreferencesKey("auto_hide_millis")
        val StartBrightnessBoost = booleanPreferencesKey("start_brightness_boost")
        val StartMirrored = booleanPreferencesKey("start_mirrored")
        val Theme = stringPreferencesKey("theme")
    }
}
