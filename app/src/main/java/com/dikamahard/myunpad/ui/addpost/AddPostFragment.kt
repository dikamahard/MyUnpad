package com.dikamahard.myunpad.ui.addpost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dikamahard.myunpad.databinding.FragmentAddPostBinding
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val db = Firebase.database
    val mAUth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addPostViewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)

        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        addPostViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnUpload.setOnClickListener {

            val id = binding.rgKategori.checkedRadioButtonId
            lateinit var kategori: String

            when(id) {
                binding.rbKampus.id -> kategori = "Kampus"
                binding.rbFakultas.id -> kategori = "Fakultas"
                binding.rbProdi.id -> kategori = "Prodi"
            }
            val title = binding.etJudul.text.toString()
            val content = binding.etKonten.text.toString()
            val penulis = mAUth.currentUser?.uid

            val repo = FirebaseRepository(mAUth, db)

            val post = Post(judul = title, konten = content, penulis!!, kategori = kategori)
            CoroutineScope(Dispatchers.Main).launch {
                repo.createPost(post)
                Toast.makeText(context, "Post Berhasil", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}