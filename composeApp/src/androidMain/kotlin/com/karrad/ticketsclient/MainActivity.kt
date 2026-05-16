package com.karrad.ticketsclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.store.TokenStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        installUncaughtExceptionHandler()
        TokenStore.init(this)
        AppSession.restoreFromStore()
        AppSession.appVersion = BuildConfig.VERSION_NAME
        AppSession.userId?.let { CrashReporter.setUserId(it) }
        initContainer(BuildConfig.BASE_URL)
        handleDeepLink(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent, warmStart = true)
    }

    private fun handleDeepLink(intent: Intent?, warmStart: Boolean = false) {
        val uri = intent?.data ?: return
        // https://visit-kalmykia.ru/events/{eventId}
        val segments = uri.pathSegments
        if (segments.size >= 2 && segments[0] == "events") {
            val eventId = segments[1]
            if (warmStart) {
                AppSession.liveDeepLinkEventId.value = eventId
            } else {
                AppSession.pendingDeepLinkEventId = eventId
            }
        }
    }

    private fun installUncaughtExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            CrashReporter.log(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
