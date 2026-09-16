package utils


import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SHOW_NOTES_BY_DEFAULT = "show_notes_by_default"
        const val KEY_THEME_MODE = "theme_mode"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    var showNotesByDefault: Boolean
        get() = prefs.getBoolean(KEY_SHOW_NOTES_BY_DEFAULT, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_NOTES_BY_DEFAULT, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value).apply()
            applyTheme()
        }

    fun applyTheme() {
        val mode = when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}