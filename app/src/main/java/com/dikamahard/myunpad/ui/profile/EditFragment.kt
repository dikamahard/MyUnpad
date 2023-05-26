package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentEditBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class EditFragment : Fragment() {

    companion object {
        val TAG = "EDITFRAGMENT"
    }

    private lateinit var binding: FragmentEditBinding
    val userAuth = FirebaseAuth.getInstance().currentUser
    val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val judul = EditFragmentArgs.fromBundle(arguments as Bundle).judul
        val konten = EditFragmentArgs.fromBundle(arguments as Bundle).konten
        val postId = EditFragmentArgs.fromBundle(arguments as Bundle).postId

        // predefined text
        binding.etJudul.setText(judul)
        binding.etKonten.setText(konten)

        // get post category from categoryPost db
        //CoroutineScope(Dispatchers.Main).launch {
//            dbRef.child(FirebaseRepository.CATEGORYPOST).orderByChild(postId).equalTo(true).get().addOnSuccessListener { snapshot ->
//                for (data in snapshot.children) {
//                    Log.d(TAG, "onViewCreated: ${data.value}")
//                    Log.d(TAG, "onViewCreated: ${snapshot.value}")
//                }
//            }.addOnFailureListener { error ->
//                Log.d(TAG, "onViewCreated: ${error.message}")
//            }

        val kategoriGroup = binding.rgKategori
        //lateinit var oldKategori: String
        var categoryId: String? = null
        dbRef.child(FirebaseRepository.CATEGORYPOST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: ${snapshot.value.toString()}")

                for (data in snapshot.children) {
                    categoryId = data.key
                    Log.d(TAG, "onDataChange: loop")
                    if (data.hasChild(postId)) {
                        Log.d(TAG, "onDataChange: $categoryId")
                        break
                    }
                   
                }

                // predefined checked category
                when(categoryId.toString()[0]) {
                    'k' -> {
                        kategoriGroup.check(binding.rbKampus.id)
                        //oldKategori = "kampus"
                    }
                    'f' -> {
                        kategoriGroup.check(binding.rbFakultas.id)
                        //oldKategori = "fakultas"
                    }
                    'p' -> {
                        kategoriGroup.check(binding.rbProdi.id)
                        //oldKategori = "prodi"
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //}

        Log.d(TAG, "onDataChange hasil: $categoryId")





        binding.btnEdit.setOnClickListener {
            val updateJudul = binding.etJudul.text.toString()
            val updateKonten = binding.etKonten.text.toString()


            Log.d(TAG, "onDataChange pilih kategori: ${kategoriGroup.checkedRadioButtonId == binding.rbKampus.id}")


            // todo : update the category
            /*
            - delete from old categoryPost
            - add to new categoryPost
            - update category value on posts db
             */

            val id = kategoriGroup.checkedRadioButtonId
            lateinit var newCategoryId: String
            lateinit var newKategori: String
            lateinit var categoryName: String

            // get newCategory
            when(id) {
                binding.rbKampus.id -> newKategori = "kampus"
                binding.rbFakultas.id -> newKategori = "fakultas"
                binding.rbProdi.id -> newKategori = "prodi"
            }


            CoroutineScope(Dispatchers.IO).launch {
                // get categoryName
                val categoryNameRef = dbRef.child(FirebaseRepository.USER).child(userAuth!!.uid).child(newKategori).get().await()
                categoryName = categoryNameRef.value.toString()

                // get categoryId
                val idSnapshot = dbRef.child(FirebaseRepository.CATEGORY).child(newKategori).orderByChild("name").equalTo(categoryName).get().await()
                for (data in idSnapshot.children) {
                    newCategoryId = data.key!!
                    break
                }

                // del from old categoryPost using oldCategoryId
                dbRef.child(FirebaseRepository.CATEGORYPOST).child(categoryId!!).child(postId).removeValue().addOnSuccessListener {
                    Log.d(TAG, "onViewCreated: BERHASIL HAPUS kategori lama")
                }.addOnFailureListener {
                    Log.d(TAG, "onViewCreated: GAGAL HAPUS kategori lama")
                }

                // add to new categoryPost
                val updateCategoryPost = mapOf<String, Boolean>(
                    postId to true
                )
                dbRef.child("${FirebaseRepository.CATEGORYPOST}/$newCategoryId").updateChildren(updateCategoryPost)

                //update category value on posts db

                // define the updates
                val updates = mapOf(
                    "judul" to updateJudul,
                    "konten" to updateKonten,
                    "kategori" to categoryName
                    // gambar
                )

                // update post from posts db
                dbRef.child(FirebaseRepository.POST).child(postId).updateChildren(updates).addOnSuccessListener {
                    findNavController().popBackStack()
                    Toast.makeText(context, "Update Berhasil", Toast.LENGTH_SHORT).show()
                }
            }


        }


    }
}