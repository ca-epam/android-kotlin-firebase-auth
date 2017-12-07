package com.adrian.project.ui.login.controller

import android.util.Log
import com.adrian.project.R
import com.adrian.project.ui.login.view.LoginActivity
import com.adrian.project.ui.login.view.LoginActivityRouter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */

class DefaultFirebaseAuthenticationController constructor(val activity: LoginActivity, val router: LoginActivityRouter) : FirebaseAuthenticationController {


    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var googleSignInOptions: GoogleSignInOptions;
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun checkCurrentUser() {
        if (firebaseAuth?.currentUser != null) {
            router.navigateToApp()
        }
    }

    override fun onLoginClicked(email: String, password: String) {
        router.showProgessBar()

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        router.hideProgessBar()
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length < 6) {
                                router.toast(LoginActivity.MINIMUM_PASSWORD)
                            } else {
                                router.toast(LoginActivity.AUTH_FAILED)
                            }
                        } else {
                            router.navigateToApp()
                        }
                    }
                })
    }

    override fun onResetPasswordClicked() {
        router.navigateToResetPasswordActivity()
    }

    override fun onSignupButtonClicked() {
        router.navigateToSignupActivity()
    }


    // GOOGLE login

    override fun setupGoogleLogin() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    router.hideProgessBar()
                    if (!task.isSuccessful()) {
                        if (password.length < 6) {
                            router.toast(LoginActivity.MINIMUM_PASSWORD)
                        } else {
                            router.toast(LoginActivity.AUTH_FAILED)
                        }
                    } else {
                        router.navigateToApp()
                    }
                }
    }

    override fun onGoogleLoginClicked() {
        val signInIntent = googleSignInClient.getSignInIntent()
        router.openGoogleLoginPopup(signInIntent)
    }

    override fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.e("LOG", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        authoriseWithFirebase(credential)
        signInWithCredential(credential)
    }

    /**
     * Call this method, to sign in with google account
     */
    private fun signInWithCredential(credential: AuthCredential) {

        // this will override the email-pwd account if email is exist.
        // Need to check if email is already exist  with email-pwd pair !!!!!!!!
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("LOG", "signInWithCredential:success")
                        val user = firebaseAuth.currentUser
                        router.navigateToApp()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("LOG", "signInWithCredential:failure", task.exception)
                        router.toast(LoginActivity.AUTH_FAILED)
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
        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                router.finishActivity()
            } else {
                val exception = task.exception
                if (exception == null) {
                    router.toast(LoginActivity.AUTH_FAILED)
//                    tvTextLog.text = "Firebase Authorisation Failed: Unknown reason"
                    return@addOnCompleteListener
                }
                if (exception is FirebaseAuthUserCollisionException) {
                    signInNewUser(credential)
                } else {
                    router.toast(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL, exception.localizedMessage)
//                    tvTextLog.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                }
            }
        }
    }

    private fun signInNewUser(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        router.finishActivity()
                    } else {
                        val exception = task.exception
                        if (exception == null) {
//                            tvTextLog.text = "Firebase Authorisation Failed: Unknown reason"
                            router.toast(LoginActivity.AUTH_FAILED)
                            return@addOnCompleteListener
                        }
                        if (exception is FirebaseAuthUserCollisionException) {
                            router.toast(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL)
//                            tvTextLog.text = context.getString(R.string.already_create_account_with_this_email)
                        } else {
                            router.toast(LoginActivity.ALREADY_CREATED_ACCOUNT_WITH_THI_EMAIL, exception.localizedMessage)
//                            tvTextLog.text = "Firebase Authorisation Failed: ${exception.localizedMessage}"
                        }
                    }
                }
    }
}