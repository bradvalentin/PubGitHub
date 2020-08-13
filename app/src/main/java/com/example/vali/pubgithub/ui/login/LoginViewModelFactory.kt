package com.example.vali.pubgithub.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginViewModelFactory @Inject constructor(private val firebaseAuth: FirebaseAuth): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java!!)) {
            LoginViewModel(this.firebaseAuth) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}