package com.navher.myapplication.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.navher.myapplication.R
import com.navher.myapplication.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen (authViewModel: AuthViewModel,
                 onLoginSuccess: () -> Unit) {

    val email = authViewModel.emailState
    val otpSent by authViewModel.otpSent.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val otpValue = remember { TextFieldState() }
    val isEmailValid = remember(email.text) {
        email.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
    }

    LaunchedEffect(otpValue.text) {
        if (otpValue.text.length == 6) {
            authViewModel.verifyOTP(otpValue.text.toString(), onLoginSuccess)
        }
    }


    Scaffold (
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton =
            {
                if (!otpSent && !isLoading) {
                    FloatingActionButton(
                        onClick = {
                            if (isEmailValid) {
                                authViewModel.sendOTP()
                            }
                        },
                        containerColor = if (isEmailValid) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Enviar código",
                            tint = if (isEmailValid) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .rotate(180f)
                        )
                    }
                }
            }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
        ) {
            Text(
                text = "Introduce tu correo electrónico",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                state = email,
                label = { Text("Correo electrónico") },
                lineLimits = TextFieldLineLimits.SingleLine,
                enabled = !otpSent && !isLoading,
                isError = email.text.isNotEmpty() && !isEmailValid,
                supportingText = {
                    if (email.text.isNotEmpty() && !isEmailValid) {
                        Text("Introduce un correo electrónico válido.")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(112.dp))

            AnimatedVisibility(
                visible = otpSent,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                OTPForm(
                    otpValue
                )
            }


        }

    }
}

@Composable
fun OTPForm(otpValue: TextFieldState) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Hemos enviado un código de 6 dígitos a tu correo. Introdúcelo a continuación.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
        )
        BasicTextField(
            state = otpValue,
            inputTransformation = InputTransformation.maxLength(6)
                .then(DigitOnlyInputTransformation()),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            decorator = { innerTextField ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(6) { index ->
                        val char = otpValue.text.getOrNull(index)?.toString() ?: ""
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            ,
                            contentAlignment = Alignment.Center,

                            ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(112.dp))
    }
}

class DigitOnlyInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().isDigitsOnly()) {
            revertAllChanges()
        }
    }
}