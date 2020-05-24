package com.example.m3u8creator


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class CustomAdapter(var dataset: Dataset) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>(){

    lateinit var clickListener: OnItemClickListener
    lateinit var longClickListerner : OnItemLongClickListener

    //VuewHolder
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        //val sampleImg = view.sampleImg
        val sampleTxt = view.checkedTextView//sampleTxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.recyclerview_item, parent, false)
        return CustomViewHolder(item)
    }

    // ?
    override fun getItemCount(): Int {
        return dataset.size
    }

    //
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.view.checkedTextView.text = dataset.title[position]
        if (dataset.selected[position]) {
            holder.view.LinearLayout.setBackgroundColor(Color.YELLOW)
            //holder.view.checkedTextView.setBackgroundColor(Color.YELLOW)
        }
        else {
            holder.view.LinearLayout.setBackgroundColor(Color.TRANSPARENT)
            //holder.view.checkedTextView.setBackgroundColor(Color.TRANSPARENT)
        }

        // short
        holder.view.setOnClickListener {
            clickListener.onItemClickListener(it, position, dataset.title[position], Constant.COLUMN_TV)
        }

        holder.view.buttonUp.setOnClickListener {
            clickListener.onItemClickListener(it, position, dataset.title[position], Constant.COLUMN_BOTTON_UP)
        }

        holder.view.buttonDown.setOnClickListener {
            clickListener.onItemClickListener(it, position, dataset.title[position], Constant.COLUMN_BOTTON_DOWN)
        }


        // long
        holder.view.setOnLongClickListener{
            longClickListerner.onItemLongClickListener(it, position, Constant.COLUMN_TV)
            true
        }

        holder.view.buttonUp.setOnLongClickListener {
            longClickListerner.onItemLongClickListener(it, position, Constant.COLUMN_BOTTON_UP)
            true
        }

        holder.view.buttonDown.setOnLongClickListener{
            longClickListerner.onItemLongClickListener(it, position, Constant.COLUMN_BOTTON_DOWN)
            true
        }


    }

    // Interface
    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, clickedText: String, column: Int)
    }

    interface  OnItemLongClickListener {
        fun onItemLongClickListener(view: View, position: Int, column: Int)
    }

    // Listener
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.clickListener = listener
    }
    fun setOnItemLongClickListerner(listener: OnItemLongClickListener){
        this.longClickListerner = listener
    }
}