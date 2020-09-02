package com.example.vali.pubgithub.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vali.pubgithub.data.entity.Owner
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

const val AVATAR_URL_TEXT = "avatar_url"

class LoginViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    val loginExistingUser: LiveData<Task<AuthResult>> = MutableLiveData<Task<AuthResult>>()
    private fun setNewTask(task: Task<AuthResult>) {
        (loginExistingUser as MutableLiveData).value = task
    }

    val loginNewUser: LiveData<FirebaseAuth> = MutableLiveData<FirebaseAuth>()
    private fun setNewUser(task: FirebaseAuth) {
        (loginNewUser as MutableLiveData).value = task
    }

    val loginUserSuccess: LiveData<Owner> = MutableLiveData<Owner>()
    private fun setNewOwner(owner: Owner) {
        (loginUserSuccess as MutableLiveData).value = owner
    }

    fun checkUserLogin() {

        firebaseAuth.pendingAuthResult?.let { pendingResultTask ->
            setNewTask(pendingResultTask)

        } ?: run {
            setNewUser(firebaseAuth)
        }

    }

    fun getUserInfo(authResult: AuthResult) {
        val username = authResult.additionalUserInfo?.username
        val avatar =
            authResult.additionalUserInfo?.profile?.get(AVATAR_URL_TEXT) as? String
        val owner = Owner(username, avatar)
        setNewOwner(owner)
    }
}