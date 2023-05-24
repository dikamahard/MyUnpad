package com.dikamahard.myunpad.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    companion object {
        val TAG = "DetailFragment"
    }

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val judul = DetailFragmentArgs.fromBundle(arguments as Bundle).judul
        Log.d(TAG, "onViewCreated: $judul")
        val konten = DetailFragmentArgs.fromBundle(arguments as Bundle).konten
        val penulis = DetailFragmentArgs.fromBundle(arguments as Bundle).penulis

        binding.tvJudul.text = judul
        binding.tvKonten.text = konten
        binding.tvPenulis.text = penulis

        // todo : add bookmark feature


    }


}