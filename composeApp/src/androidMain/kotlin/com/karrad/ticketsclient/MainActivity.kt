package com.karrad.ticketsclient

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
        AppSession.userId?.let { CrashReporter.setUserId(it) }
        initContainer(BuildConfig.BASE_URL)

        setContent {
            App()
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
