package com.example.vali.pubgithub.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vali.pubgithub.data.entity.Owner
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val firebaseAuth: FirebaseAuth): ViewModel() {

    val loginExistingUser = MutableLiveData<Task<AuthResult>>()
    val loginNewUser = MutableLiveData<FirebaseAuth>()
    val loginUserSuccess = MutableLiveData<Owner>()

    fun checkUserLogin() {

        val pendingResultTask =  firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            loginExistingUser.value = pendingResultTask
        } else {
            loginNewUser.value = firebaseAuth
        }
    }

    fun getUserInfo(authResult: AuthResult) {
        val username = authResult.additionalUserInfo?.username
        val avatar = authResult.additionalUserInfo?.profile?.get("avatar_url") as? String
        val owner = Owner(username, avatar)
        loginUserSuccess.value = owner
    }
}