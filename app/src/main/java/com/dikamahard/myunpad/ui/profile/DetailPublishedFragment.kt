package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.databinding.FragmentDetailPublishedBinding


class DetailPublishedFragment : Fragment() {

    private lateinit var binding: FragmentDetailPublishedBinding

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
    }

}