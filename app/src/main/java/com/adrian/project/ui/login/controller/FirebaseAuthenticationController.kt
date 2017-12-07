package com.adrian.project.ui.login.controller

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface FirebaseAuthenticationController {

    fun checkCurrentUser()

    fun signInWithEmailAndPassword(email: String, password: String);

}