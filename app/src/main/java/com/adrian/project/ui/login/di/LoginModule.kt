package com.adrian.project.ui.login.di

import com.adrian.kotlinrecyclerviewdagger.main.di.ActivityScope
import com.adrian.project.ui.login.controller.DefaultFirebaseAuthenticationController
import com.adrian.project.ui.login.controller.FirebaseAuthenticationController
import com.adrian.project.ui.login.model.LoginModel
import com.adrian.project.ui.login.view.FirebaseAuthenticationListener
import com.adrian.project.ui.login.view.LoginActivity
import com.adrian.project.ui.login.view.LoginActivityRouter
import dagger.Module
import dagger.Provides

/**
 * Created by cadri on 2017. 11. 25..
 */

@Module
class LoginModule {

    @Provides
    @ActivityScope
    fun provideLoginModel() = LoginModel()

    @Provides
    @ActivityScope
    fun provideLoginActivityRouter(loginActivity: LoginActivity): LoginActivityRouter
            = loginActivity

    @Provides
    @ActivityScope
    fun provideFirebaseActionListener(loginActivity: LoginActivity): FirebaseAuthenticationListener
            = loginActivity

    @Provides
    @ActivityScope
    fun provideFirebaseAuthenticationController(activity: LoginActivity, firebaseAuthenticationListener: FirebaseAuthenticationListener): FirebaseAuthenticationController
            = DefaultFirebaseAuthenticationController(activity, firebaseAuthenticationListener)

}