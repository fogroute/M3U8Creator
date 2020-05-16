package com.example.m3u8creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class MainFragmentRead : Fragment() {

    var dataset = Dataset()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main_read,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
