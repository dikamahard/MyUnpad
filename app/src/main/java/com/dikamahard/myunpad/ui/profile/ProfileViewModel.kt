package com.dikamahard.myunpad.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    companion object {
        val TAG = "PROFILEVIEWMODEL"
    }

    private val dbRef = Firebase.database.reference
    private val userAuth = FirebaseAuth.getInstance().currentUser

    private val _listPublished = MutableLiveData<List<Post>>()
    val listPublished = _listPublished

    private val _listPublishedId = MutableLiveData<List<String>>()
    val listPublishedId = _listPublishedId

    val repo = FirebaseRepository(FirebaseAuth.getInstance(), Firebase.database)


    suspend fun getPublished() {
        val listPost = mutableListOf<Post>()




        // get the user postId list
        val snapshotIdPost = dbRef.child(FirebaseRepository.USERPOST).child(userAuth!!.uid).get().await()
        val postIds = snapshotIdPost.children.map { it.key }
        // reverse the id position according the position of rv
        val reverseIds = postIds.asReversed()
        _listPublishedId.postValue(reverseIds as List<String>)



        // get the post from posts db based on the user postId list

//        val postListener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                listPost.clear()
//                for (post in snapshot.children) {
//                    val postId = post.key
//                    val judul = post.child("judul").value.toString()
//                    val konten = post.child("konten").value.toString()
//                    val penulis = post.child("penulis").value.toString()
//                    Log.d("GETPOST", "id = ${postId}")
//
//
//                    val postObject = Post(judul, konten, penulis)
//                    listPost.add(0, postObject)
//                    _listPublished.value = listPost
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // handle error
//                Log.d("GETPOST", "error")
//            }
//        }

        for (postId in postIds) {
            val snap = dbRef.child(FirebaseRepository.POST).child(postId!!).get().await()
            val id = snap.key
            Log.d(TAG, "getPublishedId: $id")
            val judul = snap.child("judul").value.toString()
            val konten = snap.child("konten").value.toString()
            val penulis = snap.child("penulis").value.toString()
            val kategori = snap.child("kategori").value.toString()
            val gambar = snap.child("gambar").value.toString()


            val obj = Post(judul, konten, penulis, kategori, gambar = gambar)
            listPost.add(0, obj)
            _listPublished.postValue(listPost)
        }
        Log.d("GETPOST", "list = $listPost")
    }





}