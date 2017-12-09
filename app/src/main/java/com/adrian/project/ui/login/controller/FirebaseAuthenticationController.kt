package com.adrian.project.ui.login.controller

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface FirebaseAuthenticationController {

    /**
     * Call when user click on Google login button
     */
    fun requestForGoogleAuthenticate()

    /**
     * Sign in with Email password pair via Firebase
     */
    fun signInWithEmailAndPassword(email: String, password: String);

    /**
     * Authenticate Google account via Firebase
     */
    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount)

    /**
     * Authenticate Facebook account via Firebase
     */
    fun firebaseAuthWithFacebook(/*acc: FacebookAccount */)

    /**
     * Authenticate Twitter account via Firebase
     */
    fun firebaseAuthWithTwitter(/*acc: TwitterAccount */)

    /**
     * Use this method to check whether email is already registered in Firebase
     * @return true if email is registered already
     */
    fun checkIfEmailIsRegisteredAlready(email: String): Boolean

    /**
     * Check user is logged in to the app
     * @return true if user is logged in, false if not
     */
    fun checkCurrentUser(): Boolean

}