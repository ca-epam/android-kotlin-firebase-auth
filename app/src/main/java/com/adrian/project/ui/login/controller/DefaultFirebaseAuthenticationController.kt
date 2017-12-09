package com.adrian.project.ui.login.controller

import android.util.Log
import com.adrian.project.R
import com.adrian.project.ui.login.view.FirebaseAuthenticationListener
import com.adrian.project.ui.login.view.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*


/**
 * FirebaseAuthentication controller class, to separate firebase logic from your login activity
 * Created by Adrian_Czigany on 12/7/2017.
 */

class DefaultFirebaseAuthenticationController constructor(val activity: LoginActivity, val firebaseAuthenticationListener: FirebaseAuthenticationListener) : FirebaseAuthenticationController {

    private lateinit var googleSignInOptions: GoogleSignInOptions;
    private lateinit var googleSignInClient: GoogleSignInClient

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        setupGoogleOptions()
    }

    override fun checkCurrentUser(): Boolean {
        return firebaseAuth?.currentUser != null
    }

    override fun requestForGoogleAuthenticate() {
        val signInIntent = googleSignInClient.getSignInIntent()
        firebaseAuthenticationListener.onRequestForGoogleAuthenticate(signInIntent)
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuthenticationListener.authenticationLoadingStart()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        firebaseAuthenticationListener.authenticationLoadingEnd()
                        if (task.isSuccessful()) {
                            firebaseAuthenticationListener.onSuccessfulEmailPasswordLogin()
                        } else {
                            if (password.length < 6) {
                                firebaseAuthenticationListener.errorMessage(LoginActivity.MINIMUM_PASSWORD)
                            } else {
                                firebaseAuthenticationListener.errorMessage(LoginActivity.AUTH_FAILED)
                            }
                        }
                    }
                })
    }

    override fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.e("LOG", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        // linkGoogleAccountWithExistingAccount(credential)
        signInWithCredential(credential)
    }


    override fun firebaseAuthWithFacebook() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun firebaseAuthWithTwitter() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO implement
    override fun checkIfEmailIsRegisteredAlready(email: String): Boolean {
        // val queryResult = firebaseAuth.fetchProvidersForEmail(email)
        return false
    }

    private fun signInWithCredential(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("LOG", "signInWithCredential:success")
                        val user = firebaseAuth.currentUser
                        firebaseAuthenticationListener.onSuccessfulGoogleLogin()
                    } else {
                        // if user enters wrong email.
                        // if user enters wrong password.
                        // If sign in fails, display a message to the user.
                        Log.e("LOG", "signInWithCredential:failure", task.exception)
                        firebaseAuthenticationListener.errorMessage(LoginActivity.AUTH_FAILED)
                    }
                })
    }

    /**
     * Call this method, if user signed in with email-password pair,
     * and you want to link this account with google account
     */
    private fun linkGoogleAccountWithExistingAccount(credential: AuthCredential) {
        mergeWithCurrentUser(credential)
    }

    private fun mergeWithCurrentUser(credential: AuthCredential) {
        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                firebaseAuthenticationListener.onSuccessfulGoogleLogin()
            } else {
                val exception = task.exception
                if (exception == null) {
                    firebaseAuthenticationListener.errorMessage(LoginActivity.AUTH_FAILED)
                    return@addOnCompleteListener
                }
                if (exception is FirebaseAuthUserCollisionException) {
                    signInNewUser(credential)
                } else {
                    firebaseAuthenticationListener.errorMessage(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL)
                }
            }
        }
    }

    private fun signInNewUser(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        firebaseAuthenticationListener.onSuccessfulGoogleLogin()
                    } else {
                        val exception = task.exception
                        if (exception == null) {
                            firebaseAuthenticationListener.errorMessage(LoginActivity.AUTH_FAILED)
                            return@addOnCompleteListener
                        }
                        if (exception is FirebaseAuthUserCollisionException) {
                            firebaseAuthenticationListener.errorMessage(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL)
                        } else {
                            firebaseAuthenticationListener.errorMessage(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL)
                        }
                    }
                }
    }

    private fun setupGoogleOptions() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }
}