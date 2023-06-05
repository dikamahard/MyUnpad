package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailPublishedBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DetailPublishedFragment : Fragment() {

    companion object {
        val TAG = "DETAILPUBLISHED"
    }

    private lateinit var binding: FragmentDetailPublishedBinding
    val userAuth = FirebaseAuth.getInstance().currentUser
    val dbRef = FirebaseDatabase.getInstance().reference
    private val storage = Firebase.storage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailPublishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val judul = DetailPublishedFragmentArgs.fromBundle(arguments as Bundle).judul
        val konten = DetailPublishedFragmentArgs.fromBundle(arguments as Bundle).konten
        val publishedId = DetailPublishedFragmentArgs.fromBundle(arguments as Bundle).publishedId
        val gambar = DetailPublishedFragmentArgs.fromBundle(arguments as Bundle).gambar
        Log.d("DETAILPUBLISH", "onViewCreated: gambar $gambar ")

        binding.tvJudulpublished.text = judul
        binding.tvKontenpublished.text = konten
//        binding.tvIdpublished.text = publishedId

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
                    .into(binding.ivDetailpublished)
            }

        }


        // todo : NEED DELETE FROM CATEGORY POST DB + IMAGE


        binding.btnDelete.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())

            alertDialogBuilder.setTitle("Delete Item")
            alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
            alertDialogBuilder.setPositiveButton("Delete") { dialog, which ->
                // Perform the delete operation here
                val postRef = dbRef.child(FirebaseRepository.POST).child(publishedId)
                postRef.removeValue { error, _ ->
                    if (error == null) {

                        // delete from userPost db
                        val userPostRef = dbRef.child(FirebaseRepository.USERPOST).child(userAuth!!.uid).child(publishedId)
                        userPostRef.removeValue { error, _ ->
                            if (error == null) {
                                //requireActivity().finish()
                                findNavController().popBackStack()
                                Toast.makeText(context, "Post Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Gagal : ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Gagal : ${error.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                // Cancel the delete operation

            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()



            // delete image from storage
            val deleteRef = storage.reference.child("post/$gambar")
            deleteRef.delete().addOnSuccessListener {
                Log.d(TAG, "GAMBAR DELETE BERHASIL $gambar")
            }.addOnFailureListener {
                Log.d(TAG, "GAMBAR DELETE GAGAL $gambar")

            }

        }



        binding.btnEdit.setOnClickListener {
            val toEditFragment = DetailPublishedFragmentDirections.actionDetailPublishedFragmentToEditFragment()
            toEditFragment.judul = judul
            toEditFragment.konten = konten
            toEditFragment.postId = publishedId
            toEditFragment.gambar = gambar
            findNavController().navigate(toEditFragment)

        }
    }

}