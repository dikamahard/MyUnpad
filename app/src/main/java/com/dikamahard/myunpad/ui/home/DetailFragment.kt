package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.security.auth.callback.Callback
import kotlin.math.log

class DetailFragment : Fragment() {

    companion object {
        val TAG = "DetailFragment"
    }
    private val db = Firebase.database
    private val mAuth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage



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
        val postId = DetailFragmentArgs.fromBundle(arguments as Bundle).postId
        Log.d(TAG, "onViewCreated: GAMBAR = $gambar")



        CoroutineScope(Dispatchers.Main).launch {
            // get author name
            val penulisNama = db.reference.child(FirebaseRepository.USER).child(penulisId).child("name").get().await()

            binding.tvJudul.text = judul
            binding.tvKonten.text = konten
            binding.tvPenulis.text = penulisNama.value.toString()
            //binding.tvIdDetail.text = postId

            val fragmentContext = context

            val imageRef = storage.reference.child("post/$gambar")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imgUrl = uri.toString()

//                Glide.with(requireContext())
//                    .load(imgUrl)
//                    .into(binding.ivPostdetail)

                fragmentContext?.let { context ->
                    Glide.with(context)
                        .load(imgUrl)
                        .into(binding.ivPostdetail)
                }

            }


            // load author profile
            val profileRef = storage.reference.child("profile/$penulisId")
            profileRef.downloadUrl.addOnSuccessListener { uri ->
                val imgUrl = uri.toString()
//            Glide.with(requireContext())
//                .load(imgUrl)
//                .into(binding.ivDetailpublished)

                fragmentContext?.let { context ->
                    Glide.with(context)
                        .load(imgUrl)
                        .into(binding.ivProfilpenulis)
                }
            }
        }


        // todo : add bookmark feature
        // Check if post is bookmarked, if yes then bookmarked button will be filled and the click
        // will trigger remove from bookmarke query. otherwise button will be hollow and the click
        // will trigget add to bokmark query.

        isPostBookmarked(postId) { isBookmarked ->
            if (isBookmarked) {
                // Post is already on bookmarks db
                binding.btnBookmark.setOnClickListener {

                    // delete from db
                    val bookmarkedRef = db.reference.child(FirebaseRepository.BOOKMARK).child(mAuth.currentUser!!.uid).child(postId)
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
                        postId to true
                    )

                    // add to db
                    val addBookmarkRef = db.reference.child(FirebaseRepository.BOOKMARK).child(mAuth.currentUser!!.uid)
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
        val uId = mAuth.currentUser!!.uid
        val bookmarksRef = db.reference.child(FirebaseRepository.BOOKMARK).child(uId).child(postId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "datasnapshot = ${dataSnapshot.value.toString()}")
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
                Log.d(TAG, "onCancelled: ${databaseError.message}")
                callback(false)
            }
        }

        bookmarksRef.addValueEventListener(valueEventListener)
    }


}