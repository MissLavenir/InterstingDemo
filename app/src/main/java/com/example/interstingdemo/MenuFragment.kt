package com.example.interstingdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRandom.setOnClickListener{
            view.findNavController().navigate(R.id.action_MenuFragment_to_GetRandomCount)
        }

        rankRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_dragRecyclerView)
        }

        getLuckCount.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_luckGenerator)
        }

    }

}