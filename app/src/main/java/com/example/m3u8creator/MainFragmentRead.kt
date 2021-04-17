package com.example.m3u8creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment


class MainFragmentRead : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main_read,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerSearch = activity?.findViewById<Spinner>(R.id.spinnerSearch)
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.search_mode,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSearch?.adapter = adapter
        }
        spinnerSearch?.setSelection(Constant.SPINNER_PATH_CI)

        val spinnerSelect = activity?.findViewById<Spinner>(R.id.spinnerSelect)
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.selection_mode,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSelect?.adapter = adapter
        }

    }



}
