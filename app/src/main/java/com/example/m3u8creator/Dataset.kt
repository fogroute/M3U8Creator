package com.example.m3u8creator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dataset (
    var size: Int = 0,
    var count: Int = 0,
    var title: ArrayList<String> = arrayListOf(),
    var path: ArrayList<String> = arrayListOf(),
    var selected: ArrayList<Boolean> = arrayListOf()
) : Parcelable {
    fun clear(){
        size = 0
        count = 0
        title = arrayListOf()
        path = arrayListOf()
        selected = arrayListOf()
    }

    fun add(newTitle:String, newPath : String, newSelected:Boolean) {
        size += 1
        count = if (newSelected) count+1 else count
        title.add(newTitle)
        path.add(newPath)
        selected.add(newSelected)
    }
    fun add(i: Int, newTitle:String, newPath : String, newSelected:Boolean){
        size += 1
        count = if (newSelected) count+1 else count
        title.add(i, newTitle)
        path.add(i, newPath)
        selected.add(i, newSelected)
    }

    fun swap(i:Int, j:Int) {
        if (i>=0 && i<size && j>0 && j<size) {
            val tempTitle = title[i]
            val tempPath = path[i]
            val tempSelected = selected[i]
            title[i] = title[j]
            path[i] = path[j]
            selected[i] = selected[j]
            title[j] = tempTitle
            path[j] = tempPath
            selected[j] = tempSelected
        }
    }

    fun removeAt(i: Int){
        size -= 1
        count = if (selected[i]) count-1 else count
        title.removeAt(i)
        path.removeAt(i)
        selected.removeAt(i)
    }



    fun removeUnused(){
        for (i in (size-1) downTo 0) {
            if (!selected[i]) {
                size -= 1
                title.removeAt(i)
                path.removeAt(i)
                selected.removeAt(i)
            }
        }
        count = size
    }

    fun selectAll (selectedAll : Boolean){
        for (i in 0..size - 1) {
            selected[i] = selectedAll
        }
        count = if (selectedAll) size else 0
    }

    fun invertSelectionStatus(){
        for (i in 0..size - 1) {
            selected[i] = !selected[i]
        }
        count = size - count
    }
}
