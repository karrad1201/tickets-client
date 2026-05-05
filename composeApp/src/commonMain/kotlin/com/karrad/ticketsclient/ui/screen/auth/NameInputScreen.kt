package com.karrad.ticketsclient.ui.screen.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.ApiException
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.CitySelectionScreen
import com.karrad.ticketsclient.ui.util.toAuthErrorMessage
import kotlinx.coroutines.launch

@Composable
fun NameInputScreen(phone: String = "", code: String = "") {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var nameValue by remember { mutableStateOf(TextFieldValue("")) }
    val name = nameValue.text
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Text(text = "Как вас зовут?", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Введите ваше имя — оно будет отображаться в профиле",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it; error = null },
            label = { Text("Имя") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
            ),
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loading = true
                error = null
                scope.launch {
                    try {
                        val response = AppContainer.authService.register(phone, code, name)
                        AppSession.login(
                            token = response.accessToken,
                            userId = response.user.id,
                            phone = response.user.phone,
                            fullName = response.user.fullName,
                            role = response.user.role,
                            avatarUrl = response.user.avatarUrl,
                            interests = response.user.interests
                        )
                        navigator.push(CitySelectionScreen)
                    } catch (e: ApiException) {
                        error = e.toAuthErrorMessage("Ошибка регистрации. Попробуйте ещё раз.")
                    } catch (e: Exception) {
                        CrashReporter.log(e)
                        error = e.toAuthErrorMessage("Ошибка регистрации. Попробуйте ещё раз.")
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = name.isNotBlank() && !loading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Продолжить", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
