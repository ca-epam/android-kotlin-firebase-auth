package com.adrian.project.ui.login.view

import android.content.Intent

/**
 * Created by Adrian_Czigany on 12/9/2017.
 */

interface FirebaseAuthenticationListener {

    fun onRequestForGoogleAuthenticate(signInIntent: Intent)

    fun onSuccessfulEmailPasswordLogin()

    fun onSuccessfulGoogleLogin()

    fun onSuccessfulFacebookLogin()

    fun onSuccessfulTwitterLogin()

    /**
     * Error message with the specified string id
     */
    fun errorMessage(stringId: Int)

    /**
     * Notify listener, that firebase authentication loading is started.
     * You should take care to call @ling{authenticationLoadingEnd} method, when loading ended
     */
    fun authenticationLoadingStart()

    /**
     * Notify listener, that firebase authentication loading is ended.
     */
    fun authenticationLoadingEnd()
}