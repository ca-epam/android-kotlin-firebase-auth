package com.adrian.project.ui.login.view

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface LoginActivityRouter {

    fun goToApp()

    fun onUserPasswordLogin()

    fun onGoogleLogin()

    fun onUnSuccessLogin()
}