package com.dikamahard.myunpad.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditViewModel : ViewModel() {
    private val dbRef = Firebase.database.reference
    private val userAuth = FirebaseAuth.getInstance().currentUser

    fun getCategoryId() {

    }
}