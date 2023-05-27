package com.dikamahard.myunpad.ui.addpost

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dikamahard.myunpad.databinding.FragmentAddPostBinding
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {

    companion object {
        val TAG = "ADDPOSTFRAGMENT"
    }

    private lateinit var binding: FragmentAddPostBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!

    val db = Firebase.database
    val mAUth = FirebaseAuth.getInstance()

    lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addPostViewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)

        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        addPostViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUnggahfoto.setOnClickListener {
            selectImage()
        }



        binding.btnUpload.setOnClickListener {
            val id = binding.rgKategori.checkedRadioButtonId
            lateinit var kategori: String

            when(id) {
                binding.rbKampus.id -> kategori = "kampus"
                binding.rbFakultas.id -> kategori = "fakultas"
                binding.rbProdi.id -> kategori = "prodi"
            }
            val title = binding.etJudul.text.toString()
            val content = binding.etKonten.text.toString()
            val penulis = mAUth.currentUser?.uid

            val repo = FirebaseRepository(mAUth, db)

            val post = Post(judul = title, konten = content, penulis!!, kategori = kategori)
            CoroutineScope(Dispatchers.Main).launch {
                repo.createPost(post, imageUri)
                //requireActivity().finish()
                findNavController().popBackStack()
                Toast.makeText(context, "Post Berhasil", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

    private fun selectImage() {

        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startActivityForResult(intent,100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.ivGambar.setImageURI(imageUri)
        }
    }
}