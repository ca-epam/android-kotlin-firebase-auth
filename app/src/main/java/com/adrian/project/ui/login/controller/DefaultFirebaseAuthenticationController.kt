package com.adrian.project.ui.login.controller

import com.adrian.project.ui.login.view.LoginActivityRouter
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Adrian_Czigany on 12/7/2017.
 */

class DefaultFirebaseAuthenticationController constructor(val router: LoginActivityRouter) : FirebaseAuthenticationController {

    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun checkCurrentUser() {
        if (firebaseAuth.currentUser != null) {
            router.goToApp()
        }
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                }
//                .addOnCompleteListener(this@LoginActivity, object : OnCompleteListener<AuthResult> {
//                    override fun onComplete(task: Task<AuthResult>) {
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        progressBar.setVisibility(View.GONE)
//                        if (!task.isSuccessful()) {
//                            // there was an error
//                            if (password.length < 6) {
//                                etPassword.error = getString(R.string.minimum_password)
//                            } else {
//                                Toast.makeText(this@LoginActivity, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
//                            }
//                        } else {
//                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }
//                    }
//                })
    }
}