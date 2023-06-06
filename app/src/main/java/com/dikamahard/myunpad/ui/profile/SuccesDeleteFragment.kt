package com.dikamahard.myunpad.ui.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dikamahard.myunpad.R

class SuccesDeleteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_succes_delete, container, false)
    }

    private val DELAY_TIME_MILLIS = 2000L // 2 seconds

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            val navCon = activity?.findNavController(R.id.nav_host_fragment_activity_main)
            navCon?.popBackStack()
            navCon?.navigate(R.id.navigation_profile)
        }, DELAY_TIME_MILLIS)

    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            findNavController().popBackStack()
//            findNavController().navigate(R.id.navigation_profile)
//        }, DELAY_TIME_MILLIS)
//    }

}