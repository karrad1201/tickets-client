# ── kotlinx.serialization ──────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes and their companions
-keep,includedescriptorclasses class com.karrad.ticketsclient.**$$serializer { *; }
-keepclassmembers class com.karrad.ticketsclient.** {
    *** Companion;
}
-keepclasseswithmembers class com.karrad.ticketsclient.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class * { *; }

# ── Ktor ───────────────────────────────────────────────────────────────────────
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**

# ── OkHttp ─────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── Voyager ────────────────────────────────────────────────────────────────────
-keep class cafe.adriel.voyager.** { *; }
-dontwarn cafe.adriel.voyager.**

# ── Compose ────────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── CameraX / ML Kit ───────────────────────────────────────────────────────────
-keep class androidx.camera.** { *; }
-keep class com.google.mlkit.** { *; }
-dontwarn androidx.camera.**
-dontwarn com.google.mlkit.**

# ── EncryptedSharedPreferences ─────────────────────────────────────────────────
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# ── App classes ────────────────────────────────────────────────────────────────
-keep class com.karrad.ticketsclient.** { *; }

# ── General ────────────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
