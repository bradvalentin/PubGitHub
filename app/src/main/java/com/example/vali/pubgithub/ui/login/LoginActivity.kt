package com.example.vali.pubgithub.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.databinding.ActivityLoginBinding
import com.example.vali.pubgithub.ui.repoList.RepoListActivity
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.example.vali.pubgithub.utils.setOnSingleClickListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject
import kotlin.random.Random


class LoginActivity : AppCompatActivity(), LoginClickInterface {

    private val provider: OAuthProvider.Builder by lazy { OAuthProvider.newBuilder(getString(R.string.github_site)) }
    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this, this.viewModeFactory).get(LoginViewModel::class.java) }

    @Inject
    lateinit var viewModeFactory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.listener = this

        provider.scopes = arrayListOf(getString(R.string.public_repo))

        observeViewModel()
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
                loginUserFail(it.localizedMessage)
            }
    }

    private fun loginNewUser(firebaseAuth: FirebaseAuth) {
        firebaseAuth
            .startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                loginViewModel.getUserInfo(it)
            }
            .addOnFailureListener {
                loginUserFail(it.localizedMessage)
            }
    }

    private fun loginUserSuccess(owner: Owner) {
        motionLayoutLoginLoading.transitionToState(R.id.start)
        SharedPreferencesHelper.saveOwner(owner, this)
        startRepoListActivity()
    }

    private fun loginUserFail(text: String) {
        motionLayoutLoginLoading.transitionToState(R.id.start)
        Toast.makeText(
            this,
            getString(R.string.some_error).plus(getString(R.string.error_text_separator)).plus(text),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startRepoListActivity() {
        startActivity(Intent(this, RepoListActivity::class.java))
        finish()
    }

    override fun onLoginClicked() {
        loginViewModel.checkUserLogin()
        motionLayoutLoginLoading.transitionToState(R.id.middle)
    }

}
