with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/auth/AuthViewModel.kt", "r") as f:
    content = f.read()

new_content = content.replace("""        supabaseManager.signIn(email, pass, object : SupabaseManager.AuthCallback {
            override fun onResult(success: Boolean, message: String?) {
                _isLoading.value = false
                if (success) {
                    _isSuccess.value = true
                } else {
                    _error.value = message ?: "حدث خطأ أثناء تسجيل الدخول"
                }
            }
        })""", """        supabaseManager.signIn(email, pass, object : SupabaseManager.AuthCallback {
            override fun onSuccess(message: String?) {
                _isLoading.value = false
                _isSuccess.value = true
            }
            override fun onError(error: String?) {
                _isLoading.value = false
                _error.value = error ?: "حدث خطأ أثناء تسجيل الدخول"
            }
        })""")

new_content = new_content.replace("""        supabaseManager.signUp(email, pass, object : SupabaseManager.AuthCallback {
            override fun onResult(success: Boolean, message: String?) {
                _isLoading.value = false
                if (success) {
                    _isSuccess.value = true
                } else {
                    _error.value = message ?: "حدث خطأ أثناء إنشاء الحساب"
                }
            }
        })""", """        supabaseManager.signUp(email, pass, object : SupabaseManager.AuthCallback {
            override fun onSuccess(message: String?) {
                _isLoading.value = false
                _isSuccess.value = true
            }
            override fun onError(error: String?) {
                _isLoading.value = false
                _error.value = error ?: "حدث خطأ أثناء إنشاء الحساب"
            }
        })""")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/auth/AuthViewModel.kt", "w") as f:
    f.write(new_content)
