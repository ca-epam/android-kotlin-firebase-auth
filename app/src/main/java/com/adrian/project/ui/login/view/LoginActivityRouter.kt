package com.adrian.project.ui.login.view

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */
interface LoginActivityRouter {

    /**
     * Method is called when login was successful, or user is logged in
     */
    fun navigateToApp()

    /**
     * Navigate to "sign up" page
     */
    fun navigateToSignupActivity()

    /**
     * Navigate to "reset password" page
     */
    fun navigateToResetPasswordActivity()

    /**
     * Show progressbar
     */
    fun showProgessBar()

    /**
     * Hide progressbar
     */
    fun hideProgessBar()
}
