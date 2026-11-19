package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.firebase.configureAppCheck
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MissaoCfoTheme
import com.example.ui.theme.ThemePreferencesRepository
import com.example.ui.theme.resolveIsDarkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val themeRepo = remember { ThemePreferencesRepository(applicationContext) }
            val themeMode by themeRepo.themeModeFlow.collectAsStateWithLifecycle(
                initialValue = AppThemeMode.AUTO
            )
            val systemDark = isSystemInDarkTheme()
            val isDark = resolveIsDarkTheme(themeMode, systemDark)
            val scope = rememberCoroutineScope()

            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )
            }

            MissaoCfoTheme(themeMode = themeMode, darkTheme = isDark) {
                AppNavigation(
                    themeMode = themeMode,
                    onSelectThemeMode = { chosen ->
                        scope.launch { themeRepo.setThemeMode(chosen) }
                    }
                )
            }
        }
    }
}
