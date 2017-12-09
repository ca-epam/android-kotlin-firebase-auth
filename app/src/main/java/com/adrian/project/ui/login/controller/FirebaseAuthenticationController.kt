package com.adrian.project.ui.login.controller

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface FirebaseAuthenticationController {

    fun checkCurrentUser()

    fun setupGoogleLogin()

    fun signInWithEmailAndPassword(email: String, password: String);

    fun onResetPasswordClicked()

    fun onLoginClicked(email: String, password: String)

    fun onSignupButtonClicked()

    fun onGoogleLoginClicked()

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount)

    fun checkIfEmailIsRegisteredAlready(email: String): Boolean
}