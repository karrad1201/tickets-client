package com.karrad.ticketsclient.data.store

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus
import platform.Foundation.NSString.Companion.create as NSStringCreate

private const val SERVICE = "com.karrad.ticketsclient"

@OptIn(ExperimentalForeignApi::class)
private fun keychainSave(account: String, value: String) {
    val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
    keychainDelete(account)
    memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)!!
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, SERVICE as CFStringRef)
        CFDictionarySetValue(query, kSecAttrAccount, account as CFStringRef)
        CFDictionarySetValue(query, kSecValueData, data)
        CFDictionarySetValue(query, kSecAttrAccessibleWhenUnlockedThisDeviceOnly, kCFBooleanTrue)
        SecItemAdd(query, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun keychainLoad(account: String): String? {
    memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)!!
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, SERVICE as CFStringRef)
        CFDictionarySetValue(query, kSecAttrAccount, account as CFStringRef)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        val result = alloc<CFTypeRefVar>()
        val status: OSStatus = SecItemCopyMatching(query, result.ptr)
        if (status != 0) return null
        val data = result.value as? NSData ?: return null
        return NSStringCreate(data, NSUTF8StringEncoding)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun keychainDelete(account: String) {
    memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)!!
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, SERVICE as CFStringRef)
        CFDictionarySetValue(query, kSecAttrAccount, account as CFStringRef)
        SecItemDelete(query)
    }
}

actual object TokenStore {
    actual fun save(snapshot: SessionSnapshot) {
        keychainSave("token", snapshot.token)
        keychainSave("userId", snapshot.userId)
        keychainSave("fullName", snapshot.fullName)
        keychainSave("phone", snapshot.phone)
        keychainSave("role", snapshot.role)
    }

    actual fun load(): SessionSnapshot? {
        val token = keychainLoad("token") ?: return null
        val userId = keychainLoad("userId") ?: return null
        return SessionSnapshot(
            token = token,
            userId = userId,
            fullName = keychainLoad("fullName") ?: "",
            phone = keychainLoad("phone") ?: "",
            role = keychainLoad("role") ?: "USER"
        )
    }

    actual fun clear() {
        listOf("token", "userId", "fullName", "phone", "role")
            .forEach { keychainDelete(it) }
    }
}
