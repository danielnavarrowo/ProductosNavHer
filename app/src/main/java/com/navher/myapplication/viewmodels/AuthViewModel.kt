package com.navher.myapplication.viewmodels

import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navher.myapplication.utils.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val dataService: DataService) : ViewModel() {
    val emailState = TextFieldState()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    // Cache para el último email y OTP rechazados
    private var lastRejectedEmail: String? = null
    private var lastRejectedOTP: String? = null
    private var lastRejectedOTPEmail: String? = null


    fun sendOTP() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentEmail = emailState.text.toString()

            // Verificar si este email ya fue rechazado previamente
            if (lastRejectedEmail == currentEmail) {
                _errorMessage.value = "Este correo no está registrado. Intenta con otro correo."
                _isLoading.value = false
                return@launch
            }

            dataService.sendOTP(currentEmail).fold(
                onSuccess = {
                    _otpSent.value = true
                    // Limpiar cache de OTP rechazado al enviar nuevo código
                    lastRejectedOTP = null
                    lastRejectedOTPEmail = null
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al enviar OTP"
                    // Si el email no está registrado, guardarlo en cache
                    if (error.message?.contains("no está registrado") == true) {
                        lastRejectedEmail = currentEmail
                    }
                }
            )
            _isLoading.value = false
        }
    }

    fun verifyOTP(token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val currentEmail = emailState.text.toString()

            // Verificar si este OTP ya fue rechazado previamente para este email
            if (lastRejectedOTP == token && lastRejectedOTPEmail == currentEmail) {
                _errorMessage.value = "Código no válido o expirado. Inténtalo de nuevo."
                _isLoading.value = false
                return@launch
            }

            dataService.verifyOTP(currentEmail, token).fold(
                onSuccess = {
                    // Si el OTP es válido, limpiar cache
                    lastRejectedEmail = null
                    lastRejectedOTP = null
                    lastRejectedOTPEmail = null
                    onSuccess()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Código incorrecto"
                    // Si el OTP es inválido, guardarlo en cache
                    if (error.message?.contains("no válido") == true || error.message?.contains("expirado") == true) {
                        lastRejectedOTP = token
                        lastRejectedOTPEmail = currentEmail
                    }
                }
            )
            _isLoading.value = false
        }
    }

    fun checkSession(onLoggedIn: () -> Unit) {
        viewModelScope.launch {
            println("Checking session from viewmodel...")
            if (dataService.isUserLoggedIn()) {
                println("User is logged in, navigating to main screen.")
                onLoggedIn()
            }
            else {
                println("No active session found.")}
        }
    }
}
