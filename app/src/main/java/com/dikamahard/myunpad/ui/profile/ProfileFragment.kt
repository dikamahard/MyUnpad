package com.dikamahard.myunpad.ui.profile

import android.app.DownloadManager.Query
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentProfileBinding
import com.dikamahard.myunpad.model.Post
import com.dikamahard.myunpad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {

    companion object {
        val TAG = "PROFILEFRAGMENT"
    }

    //private var _binding: FragmentProfileBinding? = null
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.database
    private val storage = Firebase.storage


    private lateinit var optionsMenu: Menu


    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        val textView: TextView = binding.textNotifications
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        */

//        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root


        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        binding.tvName.text = mAuth.currentUser?.displayName
        val userId = mAuth.currentUser!!.uid

        //Fetch data from db (masi ngebug, need more robust solution) mungkin karena ngambil dari online dan masih null, harus nunggu biar keambil maybe
        db.reference.child("users").child(userId).get().addOnSuccessListener {
            //binding.tvNpm.text = it.child("npm").value.toString()
            binding.tvNpm.text = userId
            binding.tvProdi.text = it.child("prodi").value.toString()
            binding.tvFakultas.text = it.child("fakultas").value.toString()
        }

        binding.rvPublishedPost.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBookmarks.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        //binding.rvPublishedPost.adapter = PublishedAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            val snapshotIdPost = db.reference.child(FirebaseRepository.USERPOST).child(mAuth.currentUser!!.uid).get().await()
            val postIds = snapshotIdPost.children.map { it.key }
            Log.d(TAG, "onViewCreated: $postIds")

            viewModel.getPublished()
            viewModel.getBookmarks()
        }

        // get profile image from db
        val fragmentContext = context
        val profileRef = storage.reference.child("profile/$userId")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            val imgUrl = uri.toString()

            fragmentContext?.let { context ->
                Glide.with(context)
                    .load(imgUrl)
                    .into(binding.ivProfile)
            }
        }



        viewModel.listPublished.observe(viewLifecycleOwner) { listPublhised ->
            Log.d(TAG, "listpublishedid: $listPublhised")
            viewModel.listPublishedId.observe(viewLifecycleOwner) { listPublishedId ->
                val adapter = PublishedAdapter(listPublhised, listPublishedId)

                adapter.setOnItemClickCallback(object : PublishedAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Post, id: String) {
                        val toDetailPublished = ProfileFragmentDirections.actionNavigationProfileToDetailPublishedFragment()
                        toDetailPublished.judul = data.judul
                        toDetailPublished.konten = data.konten
                        toDetailPublished.publishedId = id
                        // something here need debugging
                        toDetailPublished.gambar = data.gambar ?: "doge_cp"
                        ///////////////
                        findNavController().navigate(toDetailPublished)
                    }
                })

                binding.rvPublishedPost.adapter = adapter
            }
        }



/*
        viewModel.listPublished.observe(requireActivity()) { listPublished ->
            val adapter = PublishedAdapter(listPublished)
            binding.rvPublishedPost.adapter = adapter
        }

        viewModel.listPublished.observe(requireActivity()) { listPublished ->
            val adapter = PublishedAdapter(listPublished)
            binding.rvPublishedPost.adapter = adapter
        }

 */


        // TODO : BOOKMARKS RV
        viewModel.listBookmarks.observe(viewLifecycleOwner) { listBookmarks ->
            viewModel.listBookmarksId.observe(viewLifecycleOwner) { listBookmarkId ->

                val adapter = PublishedAdapter(listBookmarks, listBookmarkId)

                adapter.setOnItemClickCallback(object : PublishedAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Post, id: String) {
                        val toDetailPublished = ProfileFragmentDirections.actionNavigationProfileToDetailPublishedFragment()
                        toDetailPublished.judul = data.judul
                        toDetailPublished.konten = data.konten
                        toDetailPublished.publishedId = id
                        // something here need debugging
                        toDetailPublished.gambar = data.gambar ?: "doge_cp"
                        ///////////////
                        findNavController().navigate(toDetailPublished)
                    }
                })
                binding.rvBookmarks.adapter = adapter

            }

        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        optionsMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option -> {
                // Handle edit profile click
                findNavController().navigate(R.id.action_navigation_profile_to_settingFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

}