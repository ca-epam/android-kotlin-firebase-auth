package com.adrian.project.ui.login.di

import com.adrian.kotlinrecyclerviewdagger.main.di.ActivityScope
import com.adrian.project.ui.login.model.LoginModel
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

//    @Provides
//    @ActivityScope
//    fun provideLoginActivityRouter(loginActivity: LoginActivity): LoginActivityRouter
//            = loginActivity
//
//    @Provides
//    @ActivityScope
//    fun provideFirebaseAuthenticationController(loginActivityRouter: LoginActivityRouter): FirebaseAuthenticationController
//            = DefaultFirebaseAuthenticationController(loginActivityRouter)

}