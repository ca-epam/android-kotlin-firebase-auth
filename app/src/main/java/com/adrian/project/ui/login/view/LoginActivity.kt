package com.adrian.project.ui.login.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.adrian.project.R
import com.adrian.project.ui.main.view.MainActivity
import com.adrian.project.ui.resetpasswordactivity.view.ResetPasswordActivity
import com.adrian.project.ui.signup.view.SignupActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001

    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)

        checkCurrentUser()

        // email-password login
        signupButtonListener()
        resetPasswordButtonListener()
        loginButtonListener()

        // google login
        setupGoogleLogin()
        googleLoginButtonListener()
    }

    private fun checkCurrentUser() {
        if (firebaseAuth.currentUser != null) {
            onSuccesLogin()
        }
    }

    private fun onSuccesLogin() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
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

                progressBar.setVisibility(View.VISIBLE)

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@LoginActivity, object : OnCompleteListener<AuthResult> {
                            override fun onComplete(task: Task<AuthResult>) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE)
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length < 6) {
                                        etPassword.error = getString(R.string.minimum_password)
                                    } else {
                                        Toast.makeText(this@LoginActivity, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        })
            }
        })
    }

    private fun resetPasswordButtonListener() {
        btnResetPassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
            }
        })
    }

    private fun signupButtonListener() {
        btnSignup.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
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
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, {
                    Log.d("DEBUG", "OnConnectionFailed")
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private fun googleLoginButtonListener() {
        btnGoogleLogin.setOnClickListener(View.OnClickListener {
            val signInIntent = googleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
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
        Log.e("LOG", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        authoriseWithFirebase(credential)
        signInWithCredential(credential)
    }

    /**
     * Call this method, to sign in with google account
     */
    private fun signInWithCredential(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("LOG", "signInWithCredential:success")
                        val user = firebaseAuth.currentUser
                        onSuccesLogin()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("LOG", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    /**
     * Call this method, if user signed in with email-password pair,
     * and you want to link this account with google account
     */
    private fun authoriseWithFirebase(credential: AuthCredential) {
        mergeWithCurrentUser(credential)
    }

    private fun mergeWithCurrentUser(credential: AuthCredential) {
        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                finish()
            } else {
                val exception = task.exception
                if (exception == null) {
                    tvTextLog.text = "Firebase Authorisation Failed: Unknown reason"
                    return@addOnCompleteListener
                }
                if (exception is FirebaseAuthUserCollisionException) {
                    signInNewUser(credential)
                } else {
                    tvTextLog.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                }
            }
        }
    }

    private fun signInNewUser(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        val exception = task.exception
                        if (exception == null) {
                            tvTextLog.text = "Firebase Authorisation Failed: Unknown reason"
                            return@addOnCompleteListener
                        }
                        if (exception is FirebaseAuthUserCollisionException) {
                            tvTextLog.text = "Someone with this email has already created an account, please try a different login method"
                        } else {
                            tvTextLog.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                        }
                    }
                }
    }

    fun onError(errorMessage: String) {
        tvTextLog.setText(errorMessage)
    }

}