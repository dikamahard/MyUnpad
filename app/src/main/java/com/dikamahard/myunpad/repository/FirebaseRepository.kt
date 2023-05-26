package com.dikamahard.myunpad.repository

import android.app.Application
import android.provider.ContactsContract.Data
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    suspend fun createPost(post: Post) {
        val timestamp = System.currentTimeMillis().toString()
        val uId = userAuth!!.uid
        val postId = "$timestamp-$uId"
        val category = post.kategori
        var categoryId: String? = null
        lateinit var categoryRef: DataSnapshot

        // get the spesific category from user profile
        when(category) {
            "fakultas" -> categoryRef = dbRef.child(USER).child(uId).child("fakultas").get().await()
            "prodi" -> categoryRef = dbRef.child(USER).child(uId).child("prodi").get().await()
            else -> categoryRef = dbRef.child(USER).child(uId).child("kampus").get().await()
        }
        post.kategori = categoryRef.value.toString()

        // push to post db
        dbRef.child(POST).child(postId).setValue(post)

        // push to category post db
            // first we need to find the id of the category from firebase


        /*
        dbRef.child(CATEGORY).child(category!!.lowercase()).orderByChild("name").equalTo(post.kategori).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    categoryId = data.key
                }
                Log.d("FIREBASEREPO", "createPost id inside single value listener : ${categoryId}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that may occur
            }
        })
         */



        Log.d("FIREBASEREPO", "kategori =  ${category!!.lowercase()} , detil kategori = ${post.kategori}")


        val snapshot = dbRef.child(CATEGORY).child(category!!.lowercase()).orderByChild("name").equalTo(post.kategori).get().await()
        for (data in snapshot.children) {
            categoryId = data.key
            Log.d("FIREBASEREPO", "createPost id : ${data.value.toString()}")// name = fmipa
            break
        }
        Log.d("FIREBASEREPO", "createPost id : ${categoryId}") // f04
        Log.d("FIREBASEREPO", "createPost id : ${snapshot.key}") // fakultas

        //then push to categoryPost db
        val updateCategoryPost = mapOf<String, Boolean>(
            postId to true
        )
        if (categoryId != null) {
            dbRef.child(CATEGORYPOST).child(categoryId).updateChildren(updateCategoryPost)
        }

        // push to user post db
        val updateUserPost = mapOf<String, Boolean>(
            postId to true
        )
        dbRef.child(USERPOST).child(uId).updateChildren(updateUserPost)

    }

    // getUserFakultas
    suspend fun getFakultas(): String {
        val uId = userAuth!!.uid
        val fakultas = dbRef.child(USER).child(uId).child("fakultas").get().await()
        return fakultas.value.toString()
    }

    // getUserProdi
    suspend fun getProdi(): String {
        val uId = userAuth!!.uid
        val prodi = dbRef.child(USER).child(uId).child("prodi").get().await()
        return prodi.value.toString()
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