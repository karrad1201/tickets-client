package com.karrad.ticketsclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.karrad.ticketsclient.data.store.TokenStore
import com.karrad.ticketsclient.di.AppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        TokenStore.init(this)
        AppSession.restoreFromStore()
        AppContainer.init(useMock = BuildConfig.USE_MOCK, baseUrl = BuildConfig.BASE_URL)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
