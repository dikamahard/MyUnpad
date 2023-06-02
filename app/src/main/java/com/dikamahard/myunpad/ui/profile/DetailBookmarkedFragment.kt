package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailBookmarkedBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.dikamahard.myunpad.ui.home.DetailFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
//        binding.tvIdbookmarked.text = publishedId

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
        isPostBookmarked(publishedId) { isBookmarked ->
            if (isBookmarked) {
                // Post is already on bookmarks db
                binding.btnBookmark.setOnClickListener {

                    // delete from db
                    val bookmarkedRef = dbRef.child(FirebaseRepository.BOOKMARK).child(userAuth!!.uid).child(publishedId)
                    bookmarkedRef.removeValue {error, _->
                        if (error == null) {
                            Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show()
                            // change button to hollow
                            binding.btnBookmark.setImageResource(R.drawable.bookmark1)
                        }else {
                            Toast.makeText(context, "Gagal menghapus bookmark : ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } else {
                // Post isn't in bookmarks db
                binding.btnBookmark.setOnClickListener {

                    val bookmarkedPost = mapOf(
                        publishedId to true
                    )

                    // add to db
                    val addBookmarkRef = dbRef.child(FirebaseRepository.BOOKMARK).child(userAuth!!.uid)
                    addBookmarkRef.updateChildren(bookmarkedPost).addOnSuccessListener {
                        Toast.makeText(context, "Berhasil menambah bookmark", Toast.LENGTH_SHORT).show()
                        // change button to filled
                        binding.btnBookmark.setImageResource(R.drawable.bookmark2)
                    }.addOnFailureListener {
                        Toast.makeText(context, "Gagal menambah bookmark", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }


    }

    private fun isPostBookmarked(postId: String, callback: (Boolean) -> Unit) {
        val uId = userAuth!!.uid
        val bookmarksRef = dbRef.child(FirebaseRepository.BOOKMARK).child(uId).child(postId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(DetailFragment.TAG, "datasnapshot = ${dataSnapshot.value.toString()}")
                val isBookmarked = dataSnapshot.getValue(Boolean::class.java) ?: false
                callback(isBookmarked)
                if (isBookmarked) {
                    // Post is already bookmarked
                    binding.btnBookmark.setImageResource(R.drawable.bookmark2)
                } else {
                    // Post is not bookmarked
                    binding.btnBookmark.setImageResource(R.drawable.bookmark1)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
                Log.d(DetailFragment.TAG, "onCancelled: ${databaseError.message}")
                callback(false)
            }
        }

        bookmarksRef.addValueEventListener(valueEventListener)
    }
}