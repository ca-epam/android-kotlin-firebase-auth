package com.adrian.project.ui.main.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.adrian.project.R
import com.adrian.project.ui.login.view.LoginActivity
import com.adrian.project.ui.main.JsonPlaceholderActivity
import com.adrian.project.ui.main.model.MainModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainRouter {

    @Inject
    lateinit var mainModel: MainModel

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        dagger.android.AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        mainModel.callApiService()

        btnPostsPage.setOnClickListener { openPostsPage() }

        btnSignOut.setOnClickListener {signOut()}

        addAuthStateListener()
    }

    private fun openPostsPage() {
        val intent = android.content.Intent(this, JsonPlaceholderActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        firebaseAuth.signOut();
        finish()
    }

    private fun addAuthStateListener() {
        // this listener will be called when there is change in firebase user session
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        firebaseAuth.addAuthStateListener(authListener)
    }
}
