package com.example.vali.pubgithub.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.ui.repoList.RepoListActivity
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.example.vali.pubgithub.utils.setSafeOnClickListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.android.AndroidInjection
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    lateinit var provider: OAuthProvider.Builder
    lateinit var loginViewModel: LoginViewModel
    @Inject
    lateinit var viewModeFactory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.activity_login)

        provider = OAuthProvider.newBuilder(getString(R.string.github_site))
        val scopes = object : ArrayList<String>() {
            init {
                add(getString(R.string.public_repo))
            }
        }
        provider.scopes = scopes

        loginViewModel = ViewModelProvider(this, this.viewModeFactory).get(LoginViewModel::class.java)
        observeViewModel()
        buttonLoginBrowser.setSafeOnClickListener {
            loginViewModel.checkUserLogin()
            motionLayoutLoginLoading.transitionToState(R.id.middle)

        }
    }

    override fun onResume() {
        super.onResume()
        motionLayoutLogin.startLayoutAnimation()
    }

    private fun observeViewModel() {
        loginViewModel.loginExistingUser.observe(this, Observer { task ->
            task?.let {
                loginExistingUser(it)
            }

        })

        loginViewModel.loginNewUser.observe(this, Observer { fa ->
            fa?.let {
                loginNewUser(it)
            }

        })

        loginViewModel.loginUserSuccess.observe(this, Observer { owner ->
            owner?.let {
                loginUserSuccess(it)
            }

        })
    }

    private fun loginExistingUser(pendingResultTask: Task<AuthResult>) {
        pendingResultTask
            .addOnSuccessListener {
                loginViewModel.getUserInfo(it)
            }
            .addOnFailureListener {
                loginUserFail()
            }
    }

    private fun loginNewUser(firebaseAuth: FirebaseAuth) {
        firebaseAuth
            .startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                loginViewModel.getUserInfo(it)
            }
            .addOnFailureListener {
                loginUserFail()
            }
    }

    private fun loginUserSuccess(owner: Owner) {
        motionLayoutLoginLoading.transitionToState(R.id.start)
        SharedPreferencesHelper.saveOwner(owner, this)
        startRepoListActivity()
    }

    private fun loginUserFail() {
        motionLayoutLoginLoading.transitionToState(R.id.start)
        Toast.makeText(this, resources.getString(R.string.some_error), Toast.LENGTH_SHORT).show()
    }

    private fun startRepoListActivity() {
        val intent = Intent(this, RepoListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
