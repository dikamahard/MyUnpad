package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentPublisherProfileBinding
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.dikamahard.myunpad.ui.profile.ProfileFragment
import com.dikamahard.myunpad.ui.profile.ProfileFragmentDirections
import com.dikamahard.myunpad.ui.profile.PublishedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PublisherProfileFragment : Fragment() {

    companion object {
        val TAG = "PUBLISHEDPROFILEFRAGMENT"
    }

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.database
    private val storage = Firebase.storage

    private lateinit var binding: FragmentPublisherProfileBinding
    private lateinit var viewModel: PublisherProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPublisherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PublisherProfileViewModel::class.java]

        val publisherId = PublisherProfileFragmentArgs.fromBundle(arguments as Bundle).publisherId

        // get publisher data using publisherId
        db.reference.child(FirebaseRepository.USER).child(publisherId).get().addOnSuccessListener {
            binding.tvName.text = it.child("name").value.toString()
            binding.tvNpm.text = it.child("npm").value.toString()
            binding.tvFakultas.text = it.child("fakultas").value.toString()
            binding.tvProdi.text = it.child("prodi").value.toString()
            binding.tvBio.text = it.child("bio").value.toString()
        }

        // get profile image from db
        val fragmentContext = context
        val profileRef = storage.reference.child("profile/$publisherId")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            val imgUrl = uri.toString()

            fragmentContext?.let { context ->
                Glide.with(context)
                    .load(imgUrl)
                    .into(binding.ivProfile)
            }
        }

        // get publisher published post (need rv)
        binding.rvPublihserPost.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getPublished(publisherId)
        }

        viewModel.listPublished.observe(viewLifecycleOwner) { listPublhised ->
            Log.d(ProfileFragment.TAG, "listpublishedid: $listPublhised")
            viewModel.listPublishedId.observe(viewLifecycleOwner) { listPublishedId ->
                val adapter = PublisherAdapter(listPublhised, listPublishedId)


                adapter.setOnItemClickCallback(object : PublisherAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post, id: String) {
                        val toDetailPost = PublisherProfileFragmentDirections.actionPublisherProfileFragmentToDetailFragment()
                        toDetailPost.judul = data.judul
                        toDetailPost.konten = data.konten
                        toDetailPost.penulis = data.penulis
                        toDetailPost.gambar = data.gambar.toString()
                        toDetailPost.postId = id
                        findNavController().navigate(toDetailPost)
                    }
                })

                binding.rvPublihserPost.adapter = adapter
            }
        }


    }
}