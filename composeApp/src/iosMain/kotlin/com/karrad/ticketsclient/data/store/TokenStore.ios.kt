package com.karrad.ticketsclient.data.store

import platform.Foundation.NSUserDefaults

actual object TokenStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun save(snapshot: SessionSnapshot) {
        defaults.setObject(snapshot.token, "token")
        defaults.setObject(snapshot.userId, "userId")
        defaults.setObject(snapshot.fullName, "fullName")
        defaults.setObject(snapshot.phone, "phone")
        defaults.setObject(snapshot.role, "role")
    }

    actual fun load(): SessionSnapshot? {
        val token = defaults.stringForKey("token") ?: return null
        val userId = defaults.stringForKey("userId") ?: return null
        return SessionSnapshot(
            token = token,
            userId = userId,
            fullName = defaults.stringForKey("fullName") ?: "",
            phone = defaults.stringForKey("phone") ?: "",
            role = defaults.stringForKey("role") ?: "USER"
        )
    }

    actual fun clear() {
        listOf("token", "userId", "fullName", "phone", "role")
            .forEach { defaults.removeObjectForKey(it) }
    }
}
