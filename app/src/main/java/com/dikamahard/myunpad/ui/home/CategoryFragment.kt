package com.dikamahard.myunpad.ui.home

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


/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {

    companion object{
        const val POSITION = "position"
        const val TAG = "CategoryFragment"
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

        if(arguments?.getInt(POSITION) == 0) {
            // info kampus
            //binding.tvCategory.text = "Informasi Kampus"


//            CoroutineScope(Dispatchers.Main).launch {
//                val adapter = PostAdapter(repo.getPost())
//                binding.rvPost.adapter = adapter
//            }
            viewModel.getPostKampus()

            viewModel.listPostKampus.observe(viewLifecycleOwner){ listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        findNavController().navigate(toDetailPost)
                    }
                })
                binding.rvPost.adapter = adapter
            }




        } else if (arguments?.getInt(POSITION) == 1) {
            // info fakultas
            //binding.tvCategory.text = "Informasi Fakultas"
            viewModel.getPostFakultas(viewModel.getFakultas())

            viewModel.listPostFakultas.observe(viewLifecycleOwner) { listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        findNavController().navigate(toDetailPost)
                    }
                })
                binding.rvPost.adapter = adapter
            }


        }else if (arguments?.getInt(POSITION) == 2) {
            // info prodi
            //binding.tvCategory.text = "Informasi Prodi"
            viewModel.getPostProdi(viewModel.getProdi())

            viewModel.listPostProdi.observe(viewLifecycleOwner) { listPost ->
                val adapter = PostAdapter(listPost)
                adapter.setOnItemClickCallback(object : PostAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post) {
                        Log.d(TAG, "onItemClicked: ${data.judul}")
                        val toDetailPost = HomeFragmentDirections.actionNavigationHomeToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
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

}