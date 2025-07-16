package com.usedocetangerinaestoque.services

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit

class SessionManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val PREFS = "app_prefs"
        private const val KEY_LOGGED = "key_logged"
    }
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isLogged(): Boolean = prefs.getBoolean(KEY_LOGGED, false)

    fun setLogged(logged: Boolean) {
        prefs.edit { putBoolean(KEY_LOGGED, logged) }
    }
}