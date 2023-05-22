package com.dikamahard.myunpad.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CategoryViewModel : ViewModel() {

    private val dbRef = Firebase.database.reference
    private val userAuth = FirebaseAuth.getInstance().currentUser

    private val _listPostKampus = MutableLiveData<List<Post>>()
    val listPostKampus: LiveData<List<Post>> = _listPostKampus

    private val _listPostFakultas = MutableLiveData<List<Post>>()
    val listPostFakultas: LiveData<List<Post>> = _listPostFakultas

    private val _listPostProdi = MutableLiveData<List<Post>>()
    val listPostProdi: LiveData<List<Post>> = _listPostProdi

    val repo = FirebaseRepository(FirebaseAuth.getInstance(), Firebase.database)

    fun getProdi(): String = runBlocking {

//        CoroutineScope(Dispatchers.IO).launch {
//            val prodi = repo.getProdi()
//        }
        repo.getProdi()
    }

     fun getFakultas(): String = runBlocking {
        repo.getFakultas()
    }

    //val fakult = runBlocking { repo.getFakultas() }


    // getPostKampus

    // getPostFakultas (diambil berdasarkan fakultas pengguna)
    fun getPostFakultas(fakultas: String) {
        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostFakultas.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).orderByChild("kategori").equalTo(fakultas).addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }

    // getPostProdi (diambil berdasarkan prodi pengguna)
    fun getPostProdi(prodi: String) {
        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostProdi.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).orderByChild("kategori").equalTo(prodi).addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }

    fun getPost() {

        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostKampus.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }


    fun getPostKampus() {
        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostKampus.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).orderByChild("kategori").equalTo("Kampus").addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }

    /*
    fun getPostFakultas() {
        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostFakultas.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).orderByChild("kategori").equalTo("Fakultas").addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }

    fun getPostProdi() {
        val listPost = mutableListOf<Post>()

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                for (post in snapshot.children) {
                    val postId = post.key
                    val judul = post.child("judul").value.toString()
                    val konten = post.child("konten").value.toString()
                    val penulis = post.child("penulis").value.toString()
                    Log.d("GETPOST", "id = ${postId}")


                    val postObject = Post(judul, konten, penulis)
                    listPost.add(0, postObject)
                    _listPostProdi.value = listPost
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
                Log.d("GETPOST", "error")
            }
        }

        dbRef.child(FirebaseRepository.POST).orderByChild("kategori").equalTo("Prodi").addValueEventListener(postListener)
        Log.d("GETPOST", "list = $listPost")
    }
     */
}