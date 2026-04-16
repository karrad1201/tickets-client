package com.karrad.ticketsclient.data.store

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual object TokenStore {
    private var prefs: SharedPreferences? = null

    /** Вызывать из MainActivity.onCreate перед AppContainer.init */
    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            "secure_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
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
