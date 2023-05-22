package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dikamahard.myunpad.databinding.ActivityLoginBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.database
    val repo = FirebaseRepository(mAuth, db)
    val currentUser = mAuth.currentUser



    public override fun onStart() {
        super.onStart()
        Log.d("LOGIN", "fungsi on start")
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null){
            Log.d("LOGIN", "ada user ternyata")
            CoroutineScope(Dispatchers.IO).launch {
                val isNewUser = repo.isNewUser(currentUser.uid)
                if(isNewUser) {
                    startActivity(Intent(this@LoginActivity, CreateProfileActivity::class.java))
                }else {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("LOGIN", "fungsi on create")



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
                            Log.d("LOGIN", "login sukses lanjut cek newuser")
                            // Sign in success, update UI with the signed-in user's information
                            // kalo baru pertama login, isi data diri dulu, kalo ga langsung main
                            val currentUserr = FirebaseAuth.getInstance().currentUser

                            if (currentUserr != null) {
                                Log.d("LOGIN", "ada user")
                                db.reference.child("users").child(currentUserr.uid).child("isnew").get().addOnSuccessListener {
                                    Log.d("LOGIN", "${it.value}")
                                    Log.i("firebase", "Got value ${it.value}")
                                    if (it.value as Boolean) {
                                        Log.d("LOGIN", "user new")
                                        startActivity(Intent(this@LoginActivity, CreateProfileActivity::class.java))
                                    }else {
                                        Log.d("LOGIN", "user old")
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    }
                                }.addOnFailureListener {
                                    Log.e("firebase", "Error getting data", it)

                                }
                            }



                            /*
                            CoroutineScope(Dispatchers.Main).launch {
                                val isNewUser = repo.isNewUser(currentUser!!.uid)
                                if(isNewUser) {
                                    startActivity(Intent(this@LoginActivity, CreateProfileActivity::class.java))
                                }else {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }
                            }

                             */

//                            val isNewUser = mAuth.currentUser?.let { auth ->
//                                auth.metadata?.creationTimestamp == auth.metadata?.lastSignInTimestamp
//                            } ?: false
//
//                            if(isNewUser) {
//                                startActivity(Intent(this, CreateProfileActivity::class.java))
//                            }else {
//                                startActivity(Intent(this, MainActivity::class.java))
//                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("LOGIN", "login fail")
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}