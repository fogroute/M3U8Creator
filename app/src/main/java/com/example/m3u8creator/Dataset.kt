package com.example.m3u8creator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dataset (
    var size: Int = 0,
    var count: Int = 0,
    var rapidMode: Boolean = false,
    var rapidModeCount: Int = 0,

    var title: ArrayList<String> = arrayListOf(),
    var path: ArrayList<String> = arrayListOf(),
    var selected: ArrayList<Boolean> = arrayListOf(),
    var rapidSelected: ArrayList<Boolean> = arrayListOf()
) : Parcelable {
    fun clear(){
        size = 0
        count = 0
        rapidModeCount = 0
        title = arrayListOf()
        path = arrayListOf()
        selected = arrayListOf()
        rapidSelected = arrayListOf()
    }

    fun add(newTitle:String, newPath : String, newSelected:Boolean, newRapidSelected:Boolean) {
        size += 1
        count = if (newSelected) count+1 else count
        rapidModeCount = if (newRapidSelected) rapidModeCount+1 else rapidModeCount
        title.add(newTitle)
        path.add(newPath)
        selected.add(newSelected)
        rapidSelected.add(newRapidSelected)
    }
    fun add(i: Int, newTitle:String, newPath : String, newSelected:Boolean, newRapidSelected: Boolean){
        size += 1
        count = if (newSelected) count+1 else count
        rapidModeCount = if (newRapidSelected) rapidModeCount+1 else rapidModeCount
        title.add(i, newTitle)
        path.add(i, newPath)
        selected.add(i, newSelected)
        rapidSelected.add(i,newRapidSelected)
    }

    fun swap(i:Int, j:Int) {
        if (i>=0 && i<size && j>=0 && j<size) {
            val tempTitle = title[i]
            val tempPath = path[i]
            val tempSelected = selected[i]
            val tempRapidSelected = rapidSelected[i]
            title[i] = title[j]
            path[i] = path[j]
            selected[i] = selected[j]
            rapidSelected[i] = rapidSelected[j]
            title[j] = tempTitle
            path[j] = tempPath
            selected[j] = tempSelected
            rapidSelected[j] = tempRapidSelected
        }
    }

    fun removeAt(i: Int){
        size -= 1
        count = if (selected[i]) count-1 else count
        rapidModeCount = if (rapidSelected[i]) rapidModeCount-1 else rapidModeCount
        title.removeAt(i)
        path.removeAt(i)
        selected.removeAt(i)
        rapidSelected.removeAt(i)
    }

    fun move(fromPosition:Int, toPosition:Int) {
        if (fromPosition>=0 && fromPosition<size && toPosition>=0 && toPosition<size) {
            val tempTitle = title[fromPosition]
            val tempPath = path[fromPosition]
            val tempSelected = selected[fromPosition]
            val tempRapidSelected = rapidSelected[fromPosition]
            this.removeAt(fromPosition)
            this.add(toPosition,tempTitle,tempPath,tempSelected,tempRapidSelected)
        }
    }

    fun concentrateUpward(){
        var nextPosition = 0
        var first = true
        for (i in 0 .. size-1){
            if(rapidSelected[i]){
                rapidSelected[i] = false
                if (first) {
                    first = false
                    nextPosition = i+1
                }
                else{
                    move(i, nextPosition)
                    nextPosition += 1
                }
            }
        }
        rapidModeCount = 0
    }

    fun concentrateDownward(){
        var nextPosition = size-1
        var first = true
        for (i in size-1 downTo 0){
            if(rapidSelected[i]){
                rapidSelected[i] = false
                if (first) {
                    first = false
                    nextPosition = i-1
                }
                else{
                    move(i, nextPosition)
                    nextPosition -= 1
                }
            }
        }
        rapidModeCount = 0
    }

    fun removeUnused(){
        for (i in (size-1) downTo 0) {
            if (!selected[i]) {
                removeAt(i)
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

    fun invertSelectionStatus(position:Int){
        if(!rapidMode) {
            selected[position] = !selected[position]
            count = if (selected[position]) count + 1 else count - 1
        }
        else{
            rapidSelected[position] = !rapidSelected[position]
            rapidModeCount = if (rapidSelected[position]) rapidModeCount+1 else rapidModeCount-1
        }
    }
    fun invertSelectionStatus(fromPosition:Int, toPosition:Int){
        if (fromPosition > toPosition){
            invertSelectionStatus(toPosition, fromPosition)
        }
        else {
            var modifiedToPosition = toPosition
            if (toPosition >= size){
                modifiedToPosition = size-1
            }
            for (i in fromPosition .. modifiedToPosition){
                invertSelectionStatus(i)
            }
        }
    }
    fun invertSelectionStatus(){
        for (i in 0..size - 1) {
            selected[i] = !selected[i]
        }
        count = size - count
    }
}
