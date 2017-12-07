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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginActivityRouter {

    private val RC_SIGN_IN = 9001

    companion object {
        var MINIMUM_PASSWORD = R.string.minimum_password
        var AUTH_FAILED = R.string.auth_failed
        var ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL = R.string.already_create_account_with_this_email
    }

    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseAuthenticationController: FirebaseAuthenticationController

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)

        AndroidInjection.inject(this)

        checkCurrentUser()

        // email-password login
        signupButtonListener()
        resetPasswordButtonListener()
        loginButtonListener()

        // google login
        setupGoogleLogin()
        googleLoginButtonListener()
    }


    override fun showProgessBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgessBar() {
        progressBar.visibility = View.GONE
    }

    override fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun toast(stringId: Int) {
        Toast.makeText(this, resources.getString(stringId), Toast.LENGTH_SHORT).show()
    }

    override fun toast(stringId: Int, message: String) {
        Toast.makeText(this, resources.getString(stringId) + " " + message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToApp() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    override fun onUserPasswordLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGoogleLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUnSuccessLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openGoogleLoginPopup(intent: Intent) {
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun navigateToSignupActivity() {
        startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
    }

    override fun navigateToResetPasswordActivity() {
        startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
    }

    override fun finishActivity() {
        finish()
    }


    private fun checkCurrentUser() {
        firebaseAuthenticationController.checkCurrentUser()
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

                firebaseAuthenticationController.onLoginClicked(email, password)
            }
        })
    }

    private fun resetPasswordButtonListener() {
        btnResetPassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                firebaseAuthenticationController.onResetPasswordClicked()
            }
        })
    }

    private fun signupButtonListener() {
        btnSignup.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                firebaseAuthenticationController.onSignupButtonClicked()
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private fun setupGoogleLogin() {
        firebaseAuthenticationController.setupGoogleLogin()
    }

    private fun googleLoginButtonListener() {
        btnGoogleLogin.setOnClickListener(View.OnClickListener {
            firebaseAuthenticationController.onGoogleLoginClicked()
        })
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
                Log.w("LOG", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        firebaseAuthenticationController.firebaseAuthWithGoogle(acct)
    }

    fun onError(errorMessage: String) {
        tvTextLog.setText(errorMessage)
    }

}