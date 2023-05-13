package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dikamahard.myunpad.databinding.ActivityCreateProfileBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Type

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var db: FirebaseDatabase
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    companion object {
        const val USER_PROFILE = "Users"
        const val TAG = "CreateProfile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.database
        //val profileRef = db.reference.child(USER_PROFILE)

        val userId = mAuth.currentUser!!.uid
        val profileRef = db.reference.child("users").child(userId)

        binding.etEmail.apply {
            setText("${mAuth.currentUser?.email}")
            isEnabled = false
        }

        binding.etName.addTextChangedListener(profileTextWatcher)
        binding.etProdi.addTextChangedListener(profileTextWatcher)
        binding.etFakultas.addTextChangedListener(profileTextWatcher)
        binding.etNpm.addTextChangedListener(profileTextWatcher)


        binding.btnSimpan.setOnClickListener {
            Log.d("CREATE", "${mAuth.currentUser?.displayName}")
            Log.d("CREATE", "${mAuth.currentUser?.uid}")


            val name = binding.etName.text.toString()
            val npm = binding.etNpm.text.toString()
            val fakultas = binding.etFakultas.text.toString()
            val prodi = binding.etProdi.text.toString()
            val kontak = binding.etKontak.text.toString()
            val bio = binding.etBio.text.toString()
            val email = mAuth.currentUser?.email

            //bikin error handling kalo ada isian yg kosong

            val userData = User(name, npm, prodi, fakultas, bio, kontak, email = email)

            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }

            mAuth.currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Profile updated")
                    }
                }

            profileRef.setValue(userData) { error, _ ->
                if (error != null) {

                    Toast.makeText(this, "GAGAL, " + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "BERHASIL", Toast.LENGTH_SHORT).show()
                }
            }

            startActivity(Intent(this, MainActivity::class.java))

        }

    }


    private val profileTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val name = binding.etName.text.toString().trim()
            val npm = binding.etNpm.text.toString().trim()
            val fakultas = binding.etFakultas.text.toString().trim()
            val prodi = binding.etProdi.text.toString().trim()

            binding.btnSimpan.isEnabled = !name.isEmpty() && !npm.isEmpty() && !fakultas.isEmpty() && !prodi.isEmpty()
        }
    }

}