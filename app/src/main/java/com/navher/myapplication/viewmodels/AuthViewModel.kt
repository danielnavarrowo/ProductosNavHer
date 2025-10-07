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


    fun sendOTP() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            dataService.sendOTP(emailState.text.toString()).fold(
                onSuccess = {
                    _otpSent.value = true
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al enviar OTP"
                }
            )
            _isLoading.value = false
        }
    }

    fun verifyOTP(token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            dataService.verifyOTP(emailState.text as String, token).fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Código incorrecto"
                }
            )
            _isLoading.value = false
        }
    }

    fun checkSession(onLoggedIn: () -> Unit) {
        viewModelScope.launch {
            if (dataService.isUserLoggedIn()) {
                onLoggedIn()
            }
        }
    }
}
