package com.navher.myapplication.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.navher.myapplication.R

@Preview(showBackground = true)
@Composable
fun LoginScreen () {
    var showOTPForm by remember { mutableStateOf(false) }

    Scaffold (
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton =
            {
                FloatingActionButton(
                    onClick = { showOTPForm = !showOTPForm },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),

                ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Flecha siguiente",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .rotate(180f)

                        )
                }
            }
    ) { innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(top = 16.dp, bottom = 128.dp, start = 16.dp, end = 16.dp),
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
                state = rememberTextFieldState(),
                label = { Text("Correo electrónico") },
                lineLimits = TextFieldLineLimits.SingleLine,
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

            AnimatedVisibility(
                visible = showOTPForm,
                enter = expandVertically(
                    expandFrom = Alignment.Top
                ) + fadeIn(),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top
                ) + fadeOut()
            ) {
                OTPForm()
            }


        }

    }
}

@Composable
fun OTPForm() {
    var otpValue by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(128.dp))
        Text(
            text = "Hemos enviado un código de 6 dígitos a tu correo. Introdúcelo a continuación.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
        )

        OTPTextField(
            otpValue = otpValue,
            onOtpValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    otpValue = newValue
                }
            }
        )
    }
}

//@Composable
//fun OTPTextField(
//    otpValue: String,
//    onOtpValueChange: (String) -> Unit,
//    otpLength: Int = 6
//) {
//    BasicTextField(
//        value = otpValue,
//        onValueChange = onOtpValueChange,
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.NumberPassword
//        ),
//        decorationBox = {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
//            ) {
//                repeat(otpLength) { index ->
//                    val char = when {
//                        index < otpValue.length -> otpValue[index].toString()
//                        else -> ""
//                    }
//                    val isFocused = index == otpValue.length
//
//                    Box(
//                        modifier = Modifier
//                            .width(48.dp)
//                            .height(56.dp)
//                            .border(
//                                width = if (isFocused) 2.dp else 1.dp,
//                                color = if (isFocused)
//                                    MaterialTheme.colorScheme.primary
//                                else
//                                    MaterialTheme.colorScheme.outline,
//                                shape = RoundedCornerShape(12.dp)
//                            )
//                            .background(
//                                color = if (isFocused)
//                                    MaterialTheme.colorScheme.surfaceContainerLowest
//                                else
//                                    MaterialTheme.colorScheme.surfaceContainer,
//                                shape = RoundedCornerShape(12.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = char,
//                            style = MaterialTheme.typography.headlineMedium.copy(
//                                color = MaterialTheme.colorScheme.onBackground,
//                                textAlign = TextAlign.Center
//                            )
//                        )
//                    }
//                }
//            }
//        },
//        textStyle = TextStyle(
//            color = Color.Transparent
//        ),
//        cursorBrush = SolidColor(Color.Transparent)
//    )
//}

@Composable
fun OTPTextField(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    otpLength: Int = 6
) {
    BasicTextField(
        value = otpValue,
        onValueChange = onOtpValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        decorationBox = { innerTextField ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(otpLength) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = 1.dp,
                                color = if (char.isEmpty()) Color.Gray else Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(Color.White),
                        contentAlignment = Alignment.Center
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
}

class EmailViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set

    val emailHasErrors by derivedStateOf {
        if (email.isNotEmpty()) {
            // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    fun updateEmail(input: String) {
        email = input
    }
}

@Composable
fun ValidatingInputTextField(
    email: String,
    updateState: (String) -> Unit,
    validatorHasErrors: Boolean
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = email,
        onValueChange = updateState,
        label = { Text("Email") },
        isError = validatorHasErrors,
        supportingText = {
            if (validatorHasErrors) {
                Text("Incorrect email format.")
            }
        }
    )
}

@Preview
@Composable
fun ValidateInput() {
    val emailViewModel: EmailViewModel = viewModel<EmailViewModel>()
    ValidatingInputTextField(
        email = emailViewModel.email,
        updateState = { input -> emailViewModel.updateEmail(input) },
        validatorHasErrors = emailViewModel.emailHasErrors
    )
}