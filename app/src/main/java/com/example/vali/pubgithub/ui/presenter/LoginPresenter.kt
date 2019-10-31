package com.example.vali.pubgithub.ui.presenter

import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.ui.contract.LoginContract
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginPresenter(): LoginContract.Presenter {

    private var view: LoginContract.View? = null
    private lateinit var firebaseAuth: FirebaseAuth

    @Inject
    constructor(
        firebaseAuth: FirebaseAuth
    ): this() {
        this.firebaseAuth = firebaseAuth
    }

    override fun checkUserLogin() {

        val pendingResultTask =  firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            view?.loginExistingUser(pendingResultTask)
        } else {
            view?.loginNewUser(firebaseAuth)
        }
    }

    override fun getUserInfo(authResult: AuthResult) {
        val username = authResult.additionalUserInfo?.username
        val avatar = authResult.additionalUserInfo?.profile?.get("avatar_url") as? String
        val owner = Owner(username, avatar)
        view?.loginUserSuccess(owner)
    }

    override fun setView(view: LoginContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

}