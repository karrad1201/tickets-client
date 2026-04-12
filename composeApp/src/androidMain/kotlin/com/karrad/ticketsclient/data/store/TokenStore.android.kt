package com.karrad.ticketsclient.data.store

import android.content.Context
import android.content.SharedPreferences

actual object TokenStore {
    private var prefs: SharedPreferences? = null

    /** Вызывать из MainActivity.onCreate перед AppContainer.init */
    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    actual fun save(snapshot: SessionSnapshot) {
        prefs?.edit()
            ?.putString("token", snapshot.token)
            ?.putString("userId", snapshot.userId)
            ?.putString("fullName", snapshot.fullName)
            ?.putString("phone", snapshot.phone)
            ?.putString("role", snapshot.role)
            ?.apply()
    }

    actual fun load(): SessionSnapshot? {
        val p = prefs ?: return null
        val token = p.getString("token", null) ?: return null
        val userId = p.getString("userId", null) ?: return null
        return SessionSnapshot(
            token = token,
            userId = userId,
            fullName = p.getString("fullName", "") ?: "",
            phone = p.getString("phone", "") ?: "",
            role = p.getString("role", "USER") ?: "USER"
        )
    }

    actual fun clear() {
        prefs?.edit()
            ?.remove("token")?.remove("userId")
            ?.remove("fullName")?.remove("phone")?.remove("role")
            ?.apply()
    }
}
