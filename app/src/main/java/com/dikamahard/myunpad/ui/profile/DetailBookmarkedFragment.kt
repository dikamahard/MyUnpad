package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailBookmarkedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DetailBookmarkedFragment : Fragment() {

    private lateinit var binding: FragmentDetailBookmarkedBinding
    val userAuth = FirebaseAuth.getInstance().currentUser
    val dbRef = FirebaseDatabase.getInstance().reference
    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBookmarkedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val judul = DetailBookmarkedFragmentArgs.fromBundle(arguments as Bundle).judul
        val konten = DetailBookmarkedFragmentArgs.fromBundle(arguments as Bundle).konten
        val publishedId = DetailBookmarkedFragmentArgs.fromBundle(arguments as Bundle).bookmarkedId
        val gambar = DetailBookmarkedFragmentArgs.fromBundle(arguments as Bundle).gambar

        binding.tvJudulbookmarked.text = judul
        binding.tvKontenbookmarked.text = konten
        binding.tvIdbookmarked.text = publishedId

        val fragmentContext = context

        // load image
        val imageRef = storage.reference.child("post/$gambar")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imgUrl = uri.toString()
//            Glide.with(requireContext())
//                .load(imgUrl)
//                .into(binding.ivDetailpublished)

            fragmentContext?.let { context ->
                Glide.with(context)
                    .load(imgUrl)
                    .into(binding.ivDetailbookmarked)
            }

        }

        // TODO : unbookmark button maybe?

    }
}