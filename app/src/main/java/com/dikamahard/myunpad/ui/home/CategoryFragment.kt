package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentCategoryBinding


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

        if(arguments?.getInt(POSITION) == 0) {
            // info kampus
            binding.tvCategory.text = "Informasi Kampus"

        } else if (arguments?.getInt(POSITION) == 1) {
            // info fakultas
            binding.tvCategory.text = "Informasi Fakultas"


        }else if (arguments?.getInt(POSITION) == 2) {
            // info prodi
            binding.tvCategory.text = "Informasi Prodi"

        }

    }

}