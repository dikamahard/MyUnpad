package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailPublishedBinding
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class DetailPublishedFragment : Fragment() {

    private lateinit var binding: FragmentDetailPublishedBinding
    val userAuth = FirebaseAuth.getInstance().currentUser
    val dbRef = FirebaseDatabase.getInstance().reference

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

        binding.tvJudulpublished.text = judul
        binding.tvKontenpublished.text = konten
        binding.tvIdpublihed.text = publishedId


        // todo : NEED DELETE FROM CATEGORY POST DB
        binding.btnDelete.setOnClickListener {
            // delete from posts db
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

        binding.btnEdit.setOnClickListener {
            val toEditFragment = DetailPublishedFragmentDirections.actionDetailPublishedFragmentToEditFragment()
            toEditFragment.judul = judul
            toEditFragment.konten = konten
            toEditFragment.postId = publishedId
            findNavController().navigate(toEditFragment)

        }
    }

}