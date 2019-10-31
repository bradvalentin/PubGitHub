package com.example.vali.pubgithub.ui.contract

import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.ui.presenter.BasePresenter
import com.example.vali.pubgithub.ui.presenter.BaseView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginContract  {

    interface View : BaseView {
        fun loginUserSuccess(owner: Owner)
        fun loginUserFail()
        fun loginNewUser(firebaseAuth: FirebaseAuth)
        fun loginExistingUser(pendingResultTask: Task<AuthResult>)

    }

    interface Presenter : BasePresenter<View> {
        fun checkUserLogin()
        fun getUserInfo(authResult: AuthResult)

    }

}