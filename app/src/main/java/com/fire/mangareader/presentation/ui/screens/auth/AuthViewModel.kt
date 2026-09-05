package com.fire.mangareader.presentation.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fire.mangareader.data.network.SupabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val supabaseManager = SupabaseManager.getInstance(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _error.value = "الرجاء إدخال البريد وكلمة المرور"
            return
        }
        _isLoading.value = true
        _error.value = null
        
        supabaseManager.signIn(email, pass, object : SupabaseManager.AuthCallback {
            override fun onSuccess(message: String?) {
                _isLoading.value = false
                _isSuccess.value = true
            }
            override fun onError(error: String?) {
                _isLoading.value = false
                _error.value = error ?: "حدث خطأ أثناء تسجيل الدخول"
            }
        })
    }

    fun register(name: String, email: String, pass: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _error.value = "الرجاء تعبئة جميع الحقول"
            return
        }
        _isLoading.value = true
        _error.value = null
        
        supabaseManager.signUp(email, pass, object : SupabaseManager.AuthCallback {
            override fun onSuccess(message: String?) {
                _isLoading.value = false
                _isSuccess.value = true
            }
            override fun onError(error: String?) {
                _isLoading.value = false
                _error.value = error ?: "حدث خطأ أثناء إنشاء الحساب"
            }
        })
    }
    
    fun resetState() {
        _error.value = null
        _isSuccess.value = false
        _isLoading.value = false
    }
}
