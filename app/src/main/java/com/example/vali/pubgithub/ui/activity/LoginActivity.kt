package com.example.vali.pubgithub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.ui.contract.LoginContract
import com.example.vali.pubgithub.ui.presenter.LoginPresenter
import com.example.vali.pubgithub.ui.presenter.RepoListPresenter
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.example.vali.pubgithub.utils.setSafeOnClickListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.android.AndroidInjection
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), LoginContract.View {

    @Inject
    lateinit var loginPresenter: LoginPresenter
    lateinit var provider: OAuthProvider.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.activity_login)

        provider = OAuthProvider.newBuilder("github.com")
        val scopes = object : ArrayList<String>() {
            init {
                add("public_repo")
            }
        }
        provider.setScopes(scopes)

        loginPresenter.setView(this)

        buttonLoginBrowser.setSafeOnClickListener {
            loginPresenter.checkUserLogin()
        }
    }

    override fun loginExistingUser(pendingResultTask: Task<AuthResult>) {
        pendingResultTask
            .addOnSuccessListener {
                loginPresenter.getUserInfo(it)
            }
            .addOnFailureListener {
                loginUserFail()
            }
    }

    override fun loginNewUser(firebaseAuth: FirebaseAuth) {
        firebaseAuth
            .startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                loginPresenter.getUserInfo(it)
            }
            .addOnFailureListener {
                loginUserFail()
            }
    }

    override fun loginUserSuccess(owner: Owner) {
        SharedPreferencesHelper.saveOwner(owner, this)
        startRepoListActivity()
    }

    override fun loginUserFail() {
        Toast.makeText(this, resources.getString(R.string.some_error), Toast.LENGTH_SHORT).show()
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        loginPresenter.detachView()
        super.onDestroy()
    }

    private fun startRepoListActivity() {
        val intent = Intent(this, RepoListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
