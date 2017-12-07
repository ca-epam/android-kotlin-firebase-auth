package com.adrian.project.ui.login.view

import android.content.Intent

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface LoginActivityRouter {

    fun showProgessBar()

    fun hideProgessBar()

    fun toast(stringId: Int)

    fun toast(stringId: Int, message: String)

    fun toast(message: String)

    fun navigateToApp()

    fun onUserPasswordLogin()

    fun onGoogleLogin()

    fun onUnSuccessLogin()

    fun openGoogleLoginPopup(intent: Intent)

    fun finishActivity()

    fun navigateToSignupActivity()

    fun navigateToResetPasswordActivity()
}