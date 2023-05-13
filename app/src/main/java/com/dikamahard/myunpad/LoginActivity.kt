package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dikamahard.myunpad.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            val isNewUser = mAuth.currentUser?.let { auth ->
                auth.metadata?.creationTimestamp == auth.metadata?.lastSignInTimestamp
            } ?: false

            if(isNewUser) {
                startActivity(Intent(this, CreateProfileActivity::class.java))
            }else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvRegisternow.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val pw: String = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email can't be empty", Toast.LENGTH_LONG).show()
            } else if (pw.isEmpty()) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_LONG).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            // kalo baru pertama login, isi data diri dulu, kalo ga langsung main

                            val isNewUser = mAuth.currentUser?.let { auth ->
                                auth.metadata?.creationTimestamp == auth.metadata?.lastSignInTimestamp
                            } ?: false

                            if(isNewUser) {
                                startActivity(Intent(this, CreateProfileActivity::class.java))
                            }else {
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}