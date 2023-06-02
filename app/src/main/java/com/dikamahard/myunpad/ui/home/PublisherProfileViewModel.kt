package com.dikamahard.myunpad.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class PublisherProfileViewModel : ViewModel() {

    companion object {
        val TAG = "PUBLISHERPROFILEVIEWMODEL"
    }

    private val dbRef = Firebase.database.reference
    private val userAuth = FirebaseAuth.getInstance().currentUser

    private val _listPublished = MutableLiveData<List<Post>>()
    val listPublished = _listPublished

    private val _listPublishedId = MutableLiveData<List<String>>()
    val listPublishedId = _listPublishedId

    suspend fun getPublished(publisherId: String) {

        val listPost = mutableListOf<Post>()

        // get post id
        val snapshotIdPost = dbRef.child(FirebaseRepository.USERPOST).child(publisherId).get().await()
        val postIds = snapshotIdPost.children.map { it.key }
        //reverse the id position
        val reveresedIds = postIds.asReversed()
        _listPublishedId.postValue(reveresedIds as List<String>)



        for (postId in postIds) {
            val snap = dbRef.child(FirebaseRepository.POST).child(postId!!).get().await()
            val judul = snap.child("judul").value.toString()
            val konten = snap.child("konten").value.toString()
            val penulis = snap.child("penulis").value.toString()
            val kategori = snap.child("kategori").value.toString()
            val gambar = snap.child("gambar").value.toString()

            val obj = Post(judul, konten, penulis, kategori, gambar = gambar)
            listPost.add(0, obj)
            _listPublished.postValue(listPost)
        }

    }
}