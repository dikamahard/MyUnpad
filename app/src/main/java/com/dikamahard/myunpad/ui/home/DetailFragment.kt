package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailFragment : Fragment() {

    companion object {
        val TAG = "DetailFragment"
    }
    private val db = Firebase.database
    private val mAuth = FirebaseAuth.getInstance()
    val storage = Firebase.storage


    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val judul = DetailFragmentArgs.fromBundle(arguments as Bundle).judul
        Log.d(TAG, "onViewCreated: $judul")
        val konten = DetailFragmentArgs.fromBundle(arguments as Bundle).konten
        val penulisId = DetailFragmentArgs.fromBundle(arguments as Bundle).penulis
        val gambar = DetailFragmentArgs.fromBundle(arguments as Bundle).gambar
        Log.d(TAG, "onViewCreated: GAMBAR = $gambar")



        CoroutineScope(Dispatchers.Main).launch {
            // get author name
            val penulisNama = db.reference.child(FirebaseRepository.USER).child(penulisId).child("name").get().await()

            binding.tvJudul.text = judul
            binding.tvKonten.text = konten
            binding.tvPenulis.text = penulisNama.value.toString()
            val imageRef = storage.reference.child("post/$gambar")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imgUrl = uri.toString()
                Glide.with(requireContext())
                    .load(imgUrl)
                    .into(binding.ivPostdetail)

            }
        }



        // todo : add bookmark feature


    }


}