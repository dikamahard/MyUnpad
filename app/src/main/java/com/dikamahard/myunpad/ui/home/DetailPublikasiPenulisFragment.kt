package com.dikamahard.myunpad.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dikamahard.myunpad.R

class DetailPublikasiPenulisFragment : Fragment() {

    companion object {
        fun newInstance() = DetailPublikasiPenulisFragment()
    }

    private lateinit var viewModel: DetailPublikasiPenulisViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_publikasi_penulis, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailPublikasiPenulisViewModel::class.java)
        // TODO: Use the ViewModel
    }

}