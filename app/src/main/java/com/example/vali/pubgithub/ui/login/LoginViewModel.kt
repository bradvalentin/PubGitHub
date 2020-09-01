package com.example.vali.pubgithub.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vali.pubgithub.data.entity.Owner
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val firebaseAuth: FirebaseAuth): ViewModel() {

    private val _loginExistingUser = MutableLiveData<Task<AuthResult>>()
    val loginExistingUser: LiveData<Task<AuthResult>>
        get() = _loginExistingUser
    private val _loginNewUser = MutableLiveData<FirebaseAuth>()
    val loginNewUser: LiveData<FirebaseAuth>
        get() = _loginNewUser
    private val _loginUserSuccess = MutableLiveData<Owner>()
    val loginUserSuccess: LiveData<Owner>
        get() = _loginUserSuccess

    fun checkUserLogin() {

        val pendingResultTask =  firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            _loginExistingUser.value = pendingResultTask
        } else {
            _loginNewUser.value = firebaseAuth
        }
    }

    fun getUserInfo(authResult: AuthResult) {
        val username = authResult.additionalUserInfo?.username
        val avatar = authResult.additionalUserInfo?.profile?.get("avatar_url") as? String
        val owner = Owner(username, avatar)
        _loginUserSuccess.value = owner
    }
}