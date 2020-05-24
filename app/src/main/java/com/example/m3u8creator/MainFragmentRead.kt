package com.example.m3u8creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

        /*
        val button = activity?.findViewById<Button>(R.id.button)
        button?.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(activity, "${getString(R.string.app_name)} ${getString(R.string.app_version)}", Toast.LENGTH_SHORT).show()
                return true
            }
        })
        */
    }
}
