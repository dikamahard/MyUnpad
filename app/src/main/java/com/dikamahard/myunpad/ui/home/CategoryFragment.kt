package com.dikamahard.myunpad.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentCategoryBinding
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.FieldPosition


/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {

    companion object{
        const val POSITION = "position"
        const val TAG = "CategoryFragment"

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "post channel"
    }

    private lateinit var binding: FragmentCategoryBinding
    private val db = Firebase.database
    private val mAuth = FirebaseAuth.getInstance()
    private val repo = FirebaseRepository(mAuth, db)

    private lateinit var viewModel: CategoryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPost.layoutManager = layoutManager

        // TODO : notification?

        if(arguments?.getInt(POSITION) == 0) {
            // info kampus
            //binding.tvCategory.text = "Informasi Kampus"


//            CoroutineScope(Dispatchers.Main).launch {
//                val adapter = PostAdapter(repo.getPost())
//                binding.rvPost.adapter = adapter
//            }
            lateinit var listIdPostKampus: List<String>
            viewModel.getPostKampus()
            viewModel.listIdPostKampus.observe(viewLifecycleOwner) { listId ->
                listIdPostKampus = listId
            }
            viewModel.listPostKampus.observe(viewLifecycleOwner){ listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post, position: Int) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        toDetailPost.gambar = data.gambar.toString()
                        toDetailPost.postId = listIdPostKampus[position]
                        findNavController().navigate(toDetailPost)
                    }
                })
                binding.rvPost.adapter = adapter
            }




        } else if (arguments?.getInt(POSITION) == 1) {
            // info fakultas
            //binding.tvCategory.text = "Informasi Fakultas"
            viewModel.getPostFakultas(viewModel.getFakultas())

            lateinit var listIdPostFakultas: List<String>
            viewModel.listIdPostFakultas.observe(viewLifecycleOwner) { listId ->
                listIdPostFakultas = listId
            }

            viewModel.listPostFakultas.observe(viewLifecycleOwner) { listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post, position: Int) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        toDetailPost.gambar = data.gambar.toString()
                        toDetailPost.postId = listIdPostFakultas[position]
                        findNavController().navigate(toDetailPost)
                    }
                })
                binding.rvPost.adapter = adapter
            }


        }else if (arguments?.getInt(POSITION) == 2) {
            // info prodi
            //binding.tvCategory.text = "Informasi Prodi"
            viewModel.getPostProdi(viewModel.getProdi())

            lateinit var listIdPostProdi: List<String>
            viewModel.listIdPostProdi.observe(viewLifecycleOwner) { listId ->
                listIdPostProdi = listId
            }

            viewModel.listPostProdi.observe(viewLifecycleOwner) { listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post, position: Int) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        toDetailPost.gambar = data.gambar.toString()
                        toDetailPost.postId = listIdPostProdi[position]
                        findNavController().navigate(toDetailPost)
                    }
                })
                binding.rvPost.adapter = adapter
            }

        }

//        binding.rvPost.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = PostAdapter(repo.getPost())
//        }

    }

    // TODO : notification
    private fun sendNotification(view: View) {
        val intent = Intent()
    }

}