package com.dikamahard.myunpad.ui.profile

import android.app.DownloadManager.Query
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.database

    private lateinit var optionsMenu: Menu

    companion object {
        const val USER_PROFILE = "Users"
    }


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.tvName.text = mAuth.currentUser?.displayName
        val userId = mAuth.currentUser!!.uid

        //Fetch data from db (masi ngebug, need more robust solution) mungkin karena ngambil dari online dan masih null, harus nunggu biar keambil maybe
        db.reference.child("users").child(userId).get().addOnSuccessListener {
            binding.tvNpm.text = it.child("npm").value.toString()
            binding.tvProdi.text = it.child("prodi").value.toString()
            binding.tvFakultas.text = it.child("fakultas").value.toString()
        }

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        optionsMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option -> {
                // Handle edit profile click
                Toast.makeText(context,"Edit Profil", Toast.LENGTH_SHORT ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}