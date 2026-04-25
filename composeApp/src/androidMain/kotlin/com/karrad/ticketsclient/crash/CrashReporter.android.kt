package com.karrad.ticketsclient.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual object CrashReporter {
    actual fun log(throwable: Throwable) {
        runCatching { FirebaseCrashlytics.getInstance().recordException(throwable) }
    }

    actual fun setUserId(userId: String) {
        runCatching { FirebaseCrashlytics.getInstance().setUserId(userId) }
    }
}
