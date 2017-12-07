package com.adrian.project.ui.signup.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.adrian.project.R
import com.adrian.project.ui.main.view.MainActivity
import com.adrian.project.ui.resetpasswordactivity.view.ResetPasswordActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()

        resetPasswordButtonListener()

        singinButtonListener()

        signupButtonListener()
    }

    private fun singinButtonListener() {
        btnSignin.setOnClickListener { finish() }
    }

    private fun signupButtonListener() {
        btnSignup.setOnClickListener(View.OnClickListener {
            val email = etEmail.text.toString().trim { it <= ' ' }
            val password = etPassword.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            progressBar.visibility = View.VISIBLE
            //create user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@SignupActivity) { task ->
                        Toast.makeText(this@SignupActivity, "createUserWithEmail:onComplete:" + task.isSuccessful, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful) {
                            Toast.makeText(this@SignupActivity, "Authentication failed." + task.exception,
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                            finish()
                        }
                    }
        })
    }

    private fun resetPasswordButtonListener() {
        btnResetPassword.setOnClickListener { startActivity(Intent(this@SignupActivity, ResetPasswordActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }
}
