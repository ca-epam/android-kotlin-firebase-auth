package com.adrian.project.ui.resetpasswordactivity.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.adrian.project.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*


class ResetPasswordActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        firebaseAuth = FirebaseAuth.getInstance()

        backButtonListener()

        resetPasswordButtonListener()
    }

    private fun resetPasswordButtonListener() {
        btnResetPassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                val email = etEmail.text.toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(application, "Enter your registered email id", Toast.LENGTH_SHORT).show()
                    return
                }

                progressBar.setVisibility(View.VISIBLE)
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this@ResetPasswordActivity, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@ResetPasswordActivity, "Failed to send reset email!", Toast.LENGTH_SHORT).show()
                                }

                                progressBar.setVisibility(View.GONE)
                            }
                        })
            }
        })
    }

    private fun backButtonListener() {
        btnBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })
    }
}
