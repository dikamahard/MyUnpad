package com.dikamahard.myunpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dikamahard.myunpad.databinding.ActivityCreateProfileBinding
import com.dikamahard.myunpad.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var valueEventListener: ValueEventListener

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

        // drop down fakultas list
        val fakultasArray = resources.getStringArray(R.array.fakultas_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,fakultasArray)
        binding.spFakultas.adapter = adapter

        // drop down prodi list
        val prodiArray = mutableListOf<String>()
        val prodiRef = db.reference.child("category/prodi")

         valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("PROFILE", "data changed")
                for (prodiSnapshot in dataSnapshot.children) {
                    val prodiName = prodiSnapshot.child("name").getValue(String::class.java)
                    prodiName?.let {
                        prodiArray.add(it)
                    }
                }

                val adapter = ArrayAdapter(
                    this@CreateProfileActivity,
                    android.R.layout.simple_spinner_item,
                    prodiArray
                )

                Log.d("PROFILE", "list fakultas = $prodiArray")
                binding.spProdi.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("PROFILE", "Failed to read value: ${databaseError.toException()}")
            }
        }

        prodiRef.addListenerForSingleValueEvent(valueEventListener)

        //////////////////////////////////


        binding.etName.addTextChangedListener(profileTextWatcher)
//        binding.etProdi.addTextChangedListener(profileTextWatcher)
//        binding.etFakultas.addTextChangedListener(profileTextWatcher)
        binding.etNpm.addTextChangedListener(profileTextWatcher)


        binding.btnSimpan.setOnClickListener {
            Log.d("CREATE", "${mAuth.currentUser?.displayName}")
            Log.d("CREATE", "${mAuth.currentUser?.uid}")


            val name = binding.etName.text.toString()
            val npm = binding.etNpm.text.toString()
            val fakultas = binding.spFakultas.selectedItem.toString()
            val prodi = binding.spProdi.selectedItem.toString()
            val kontak = binding.etKontak.text.toString()
            val bio = binding.etBio.text.toString()
            val email = mAuth.currentUser?.email

            //bikin error handling kalo ada isian yg kosong

            val userData = User(name, npm, prodi, fakultas, bio, kontak, email = email, isnew = false)

            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }

            mAuth.currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Profile updated")
                    }
                }

            // Create profile to the user db
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

    override fun onStop() {
        super.onStop()
        db.reference.removeEventListener(valueEventListener)
    }


    private val profileTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val name = binding.etName.text.toString().trim()
            val npm = binding.etNpm.text.toString().trim()
//            val fakultas = binding.spFakultas.selectedItem.toString()
//            val prodi = binding.spProdi.selectedItem.toString()

            binding.btnSimpan.isEnabled = !name.isEmpty() && !npm.isEmpty() //&& !fakultas.isEmpty() && !prodi.isEmpty()
        }
    }

}