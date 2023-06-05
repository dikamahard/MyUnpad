package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dikamahard.myunpad.databinding.ActivityRegisterBinding
import com.dikamahard.myunpad.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.database



    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            Log.d("REGISTER", "jangan ini yg kepanggil")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView5.setOnClickListener {
            finish()
        }
        binding.btnRegister.setOnClickListener {

            val email: String = binding.etEmailReg.text.toString()
            val pw: String = binding.etPasswordReg.text.toString()

            // Error Handling
            if (email.isEmpty()) {
                Toast.makeText(this, "Email can't be empty", Toast.LENGTH_LONG).show()
            } else if (pw.isEmpty()) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_LONG).show()
            } else if (!isEmailUnpad(email)) {
                Toast.makeText(
                    this,
                    "Invalid email address. Only unpad.ac.id email addresses are allowed",
                    Toast.LENGTH_LONG
                ).show()
            }

            if(isEmailUnpad(email) && pw.isNotEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            Log.d("REGISTER", "akun berhawsil")
                            // bikin newUser
                            val newUser = User(email = email, isnew = true)
                            val userId = mAuth.currentUser!!.uid
                            Log.d("REGISTER", "user id = $userId")
                            val profileRef = db.reference.child("users").child(userId)
                            profileRef.setValue(newUser) { error, _ ->
                                if (error != null) {
                                    Toast.makeText(this, "Gagal Membuat Akun " + error.message, Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.d("REGISTER", "akun masuk db")
                                    Toast.makeText(this, "Berhasil Membuat Akun", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, CreateProfileActivity::class.java))
                                }
                            }

                            // lanjut login
                            Log.d("REGISTER", "start activity login")
//                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            Log.w("REGISTER", "REGISTER GAGAL", task.exception)
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
    }

    private fun isEmailUnpad(email: String): Boolean {
        val domainUnpad = "unpad.ac.id"
        return email.endsWith(domainUnpad)
    }
}