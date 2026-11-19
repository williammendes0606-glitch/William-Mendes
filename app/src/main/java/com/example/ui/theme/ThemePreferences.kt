package com.example.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppThemeMode(
    val label: String,
    val description: String
) {
    AUTO(
        label = "Automático",
        description = "Acompanha automaticamente a aparência clara ou escura configurada no Android."
    ),
    DAY(
        label = "Modo Dia",
        description = "Aparência clara com verde sage, areia, branco quente e dourado suave."
    ),
    NIGHT(
        label = "Modo Noite",
        description = "Aparência escura confortável em verde acinzentado profundo e azul acinzentado."
    );

    companion object {
        fun fromStored(raw: String?): AppThemeMode =
            entries.firstOrNull { it.name == raw } ?: AUTO
    }
}

fun resolveIsDarkTheme(mode: AppThemeMode, systemInDarkTheme: Boolean): Boolean =
    when (mode) {
        AppThemeMode.AUTO -> systemInDarkTheme
        AppThemeMode.DAY -> false
        AppThemeMode.NIGHT -> true
    }

private val Context.appearanceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "missao_cfo_appearance_prefs"
)

class ThemePreferencesRepository(private val context: Context) {

    private val themeModeKey = stringPreferencesKey("app_theme_mode")

    val themeModeFlow: Flow<AppThemeMode> = context.appearanceDataStore.data.map { prefs ->
        AppThemeMode.fromStored(prefs[themeModeKey])
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.appearanceDataStore.edit { prefs ->
            prefs[themeModeKey] = mode.name
        }
    }
}
