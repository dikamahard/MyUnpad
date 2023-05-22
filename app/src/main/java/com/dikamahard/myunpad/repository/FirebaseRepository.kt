package com.dikamahard.myunpad.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository(auth: FirebaseAuth, db: FirebaseDatabase) {

    val userAuth = auth.currentUser
    val dbRef = db.reference


    // List of child node from root reference
    //  users
    //  posts
    //  category
    //  categoryPost
    //  userPost

    companion object {
        const val USER = "users"
        const val CATEGORY = "category"
        const val POST = "posts"
        const val CATEGORYPOST = "categoryPost"
        const val USERPOST = "userPost"
        const val CATEGORY_KAMPUS = "category/kampus"
        const val CATEGORY_FAKULTAS = "category/fakultas"
        const val CATEGORY_PRODI = "category/prodi"
    }






    /*
     */

    // CreateUserProfile
    fun createProfile(user: User, uId: String) {
        dbRef.child(USER).child(uId).setValue(user)
    }

    // GetUserInfo


    // Check new user
    suspend fun isNewUser(uId: String): Boolean {

        val data = dbRef.child(USER).child(uId).child("isnew").get().await()
        val result = data.value.toString().toBoolean()
        Log.d("NEWUSER", "${result}")
        return result
    }

    // CreatePost
    fun createPost(post: Post) {
        val timestamp = System.currentTimeMillis().toString()
        val uId = userAuth!!.uid
        val postId = "$timestamp-$uId"
        dbRef.child(POST).child(postId).setValue(post)
    }


    // GetPosts
    suspend fun getPost(): List<Post> {

        val listPost = mutableListOf<Post>()


        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(postObject)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(POST).addValueEventListener(postListener)
        Log.d("GETPOST", "list = ${listPost}")



        /*
        try {
            val snapshot = dbRef.child(POST).get().await()
            for (post in snapshot.children) {
                val postId = post.key
                val judul = post.child("judul").value.toString()
                val konten = post.child("konten").value.toString()
                val penulis = post.child("penulis").value.toString()
                Log.d("GETPOST", "id = $postId")

                val postObject = Post(judul, konten, penulis)
                listPost.add(postObject)
            }
        } catch (error: Throwable) {
            // Handle error
            Log.d("GETPOST", "$error")
        }
        */

        Log.d("GETPOST", "list = $listPost")

        return listPost
    }

     fun getPostTest(): List<Post> {

        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(postObject)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(POST).addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")

        return listPost
    }


    // GetPostDetail
}