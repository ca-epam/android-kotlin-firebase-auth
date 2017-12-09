package com.adrian.project.ui.login.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.adrian.project.R
import com.adrian.project.ui.login.controller.FirebaseAuthenticationController
import com.adrian.project.ui.main.view.MainActivity
import com.adrian.project.ui.resetpasswordactivity.view.ResetPasswordActivity
import com.adrian.project.ui.signup.view.SignupActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginActivityRouter, FirebaseAuthenticationListener {

    companion object {
        var MINIMUM_PASSWORD = R.string.minimum_password
        var AUTH_FAILED = R.string.auth_failed
        var ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL = R.string.already_create_account_with_this_email
    }

    object log {
        val TAG = LoginActivity::class.java.simpleName
    }

    @Inject
    lateinit var firebaseAuthenticationController: FirebaseAuthenticationController

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_login)

        checkCurrentUser()

        // for email-password login
        signupButtonListener()
        resetPasswordButtonListener()
        loginButtonListener()

        // for google login
        googleLoginButtonListener()
    }

    override fun onRequestForGoogleAuthenticate(signInIntent: Intent) {
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onSuccessfulEmailPasswordLogin() {
        Log.e(log.TAG, "onSuccessfulEmailPasswordLogin ....")
        navigateToApp()
    }

    override fun onSuccessfulGoogleLogin() {
        Log.e(log.TAG, "onSuccessfulGoogleLogin ....")
        navigateToApp()
    }

    override fun onSuccessfulFacebookLogin() {
        // Log.e(log.TAG, "onSuccessfulFacebookLogin ....")
        // navigateToApp()
    }

    override fun onSuccessfulTwitterLogin() {
        // Log.e(log.TAG, "onSuccessfulTwitterLogin ....")
        // navigateToApp()
    }


    override fun showProgessBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgessBar() {
        progressBar.visibility = View.GONE
    }

    override fun authenticationLoadingStart() {
        showProgessBar()
    }

    override fun authenticationLoadingEnd() {
        hideProgessBar()
    }

    override fun errorMessage(stringId: Int) {
        toast(stringId)
    }

    override fun navigateToApp() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    override fun navigateToSignupActivity() {
        startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
    }

    override fun navigateToResetPasswordActivity() {
        startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Unsuccesful authenticate!", Toast.LENGTH_SHORT)
                Log.e("LOG", "Google sign in failed", e)
            }
        }
    }

    private fun checkCurrentUser() {
        if (firebaseAuthenticationController.checkCurrentUser()) {
            navigateToApp()
        }
    }

    private fun loginButtonListener() {
        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
                    return
                }

                firebaseAuthenticationController.signInWithEmailAndPassword(email, password)
            }
        })
    }

    private fun resetPasswordButtonListener() {
        btnResetPassword.setOnClickListener { navigateToResetPasswordActivity() }
    }

    private fun signupButtonListener() {
        btnSignup.setOnClickListener { navigateToSignupActivity() }
    }

    private fun googleLoginButtonListener() {
        btnGoogleLogin.setOnClickListener { firebaseAuthenticationController.requestForGoogleAuthenticate() }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        firebaseAuthenticationController.firebaseAuthWithGoogle(acct)
    }

    private fun toast(stringId: Int) {
        Toast.makeText(this, resources.getString(stringId), Toast.LENGTH_SHORT).show()
    }
}
