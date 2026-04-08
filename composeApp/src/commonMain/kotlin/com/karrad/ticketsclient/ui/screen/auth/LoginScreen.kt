package com.karrad.ticketsclient.ui.screen.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.MainScreen
import com.karrad.ticketsclient.ui.navigation.RegisterScreen
import com.karrad.ticketsclient.ui.navigation.SmsCodeScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import ticketsclient.composeapp.generated.resources.Res
import ticketsclient.composeapp.generated.resources.ic_google
import ticketsclient.composeapp.generated.resources.ic_vk

@Composable
fun LoginScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var phone by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Пропустить — top right
        TextButton(
            onClick = { navigator.replaceAll(MainScreen) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "Пропустить",
                color = Color(0xFF8E8E93),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Main form — center
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(text = "Вход", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; error = null },
                label = { Text("Номер телефона") },
                placeholder = { Text("+7 (000) 000-00-00") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    loading = true
                    error = null
                    scope.launch {
                        try {
                            AppContainer.authService.sendCode(phone)
                            navigator.push(SmsCodeScreen(isRegistration = false, phone = phone))
                        } catch (e: Exception) {
                            error = "Не удалось отправить код. Проверьте номер."
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = phone.isNotBlank() && !loading,
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
                    Text("Войти", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        // Bottom — social login + register link
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Или войти через",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                IconButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_vk),
                        contentDescription = "ВКонтакте",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Surface(
                    modifier = Modifier.size(52.dp).clip(CircleShape).border(1.dp, Color(0xFFE0E0E0), CircleShape),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_google),
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigator.push(RegisterScreen) }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF8E8E93))) { append("Нет аккаунта? ") }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Зарегистрироваться") }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
