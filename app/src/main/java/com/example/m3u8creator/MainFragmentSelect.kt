package com.example.m3u8creator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main_select.*
import java.io.*


class MainFragmentSelect :Fragment() {

    var dataset = Dataset()
    var adapter = CustomAdapter(dataset)
    val READ_REQUEST_CODE: Int = 42
    val WRITE_REQUEST_CODE: Int = 44
    lateinit var m3u8Uri: Uri

    val SEARCH_ALL : Int = 0
    val SEARCH_TITLE : Int = 1
    val SEARCH_PATH : Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main_select, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun getFileList2(mode: Int) {
            //
            val mProjection : Array<String> = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
            )
            val textInputEditText = activity?.findViewById<TextInputEditText>(R.id.textInputEditText)
            var searchString = textInputEditText?.text.toString()
            lateinit var selectionArgs: Array<String>


            //Search Mode
            var searchMode = mode
            if (searchString == null){

            }
            else{
                var selectionArg = "*" + searchString + "*"
                selectionArgs = arrayOf(selectionArg)
            }

            //Query
            var sortOrder : String?  = null
            var selectionClause: String? = null

            when (mode){
                SEARCH_ALL -> {
                    selectionClause = null
                    sortOrder = null
                }
                SEARCH_PATH -> {
                    selectionClause = "${MediaStore.Audio.Media.DATA} GLOB ?"
                    sortOrder = MediaStore.Audio.Media.DATA
                }
                SEARCH_TITLE -> {
                    selectionClause = "${MediaStore.Audio.Media.TITLE} GLOB ?"
                    sortOrder = MediaStore.Audio.Media.TITLE

                }
            }

            val cursor = this.context?.contentResolver?.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                mProjection,
                selectionClause,
                selectionArgs,
                sortOrder
            )



            //URI
            when (cursor?.count){
                null -> {
                    Toast.makeText(activity, "null", Toast.LENGTH_SHORT).show()
                }
                0 -> {
                    Toast.makeText(activity, "NOT FOUND", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(activity, "${cursor?.count} Files", Toast.LENGTH_SHORT).show()

                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            val strTitle =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            var strPath =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                            strPath = strPath.replace("^[/a-zA-Z0-9-]*(MUSIC|Music)/".toRegex(), "")
                            dataset.add(strTitle, strPath, false)
                        } while (cursor.moveToNext() )
                        cursor.close()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }


        fun openFileGui(requestCode: Int) {
            if (requestCode == READ_REQUEST_CODE) {
                val intent = Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, READ_REQUEST_CODE)
            }
            if (requestCode == WRITE_REQUEST_CODE) {
                val intent = Intent(
                    Intent.ACTION_CREATE_DOCUMENT
                ).apply {
//                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, WRITE_REQUEST_CODE)
            }

        }


//        dataset.add("sharp", "/storage/1s", false) //dummy
//        dataset.add("principal", "/storage/2p", false) //dummy
//        dataset.add("diffuse", "/storage/3d", false) //dummy
//        dataset.add("fundamental", "/storage/4f", false) //dummy


        var layoutManager = LinearLayoutManager(activity)

        simpleRecyclerView.layoutManager = layoutManager
        simpleRecyclerView.adapter = adapter
        simpleRecyclerView.setHasFixedSize(false)




        //4 commands
        var buttonC = activity?.findViewById<Button>(R.id.buttonC)
        buttonC?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.removeUnused()
                adapter.notifyDataSetChanged()
            }
        })

        var buttonClear =
            activity?.findViewById<Button>(R.id.buttonReset)  // view.findViewById<Button>(R.id.button)
        buttonClear?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.clear()
                adapter.notifyDataSetChanged()
            }
        })

        var buttonAll = activity?.findViewById<Button>(R.id.buttonAll)
        buttonAll?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.selectAll(true)
                adapter.notifyDataSetChanged()
            }
        })
        buttonAll?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                dataset.invertSelectionStatus()
                adapter.notifyDataSetChanged()
                return true
            }
        })

        var buttonZero =
            activity?.findViewById<Button>(R.id.buttonZero)  // view.findViewById<Button>(R.id.button)
        buttonZero?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.selectAll(false)
                adapter.notifyDataSetChanged()
            }
        })



        var buttonRL = activity?.findViewById<Button>(R.id.buttonRL)
        buttonRL?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(activity, "TO BE UPDATED", Toast.LENGTH_SHORT).show()
                getFileList2(SEARCH_PATH)
            }
        })
        buttonRL?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                getFileList2(SEARCH_TITLE)
                return true
            }
        })

        var buttonRF = activity?.findViewById<Button>(R.id.buttonR)
        buttonRF?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openFileGui(READ_REQUEST_CODE)
            }
        })
        buttonRF?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                dataset.clear()
                return false
            }
        })



        // Short: Save as
        // Long: Save
        var buttonOverwrite =
            activity?.findViewById<Button>(R.id.buttonOverwrite)
        buttonOverwrite?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openFileGui(WRITE_REQUEST_CODE)
            }
        })
        buttonOverwrite?.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                if (::m3u8Uri.isInitialized) {
                    writeToUri(m3u8Uri)
                    Toast.makeText(activity, "Successfully overwritten", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "No file is opened", Toast.LENGTH_SHORT).show()
                }
                return true
            }
        })




        // RecyclerView
        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClickListener(
                view: View,
                position: Int,
                clickedText: String,
                column: Int
            ) {

                if (column == 0) {
                    var selected = !dataset.selected[position]
                    adapter.notifyItemChanged(position)
                    dataset.selected[position] = selected
                }
                if (column == 1) {
                    if (position > 0) {
                        dataset.swap(position - 1, position)
                        adapter.notifyItemRangeChanged(position - 1, 2)
                    }
                }
                if (column == 2) {
                    if (position < dataset.size - 1) {
                        dataset.swap(position, position + 1)
                        adapter.notifyItemRangeChanged(position, 2)
                    }
                }

            }
        })

        adapter.setOnItemLongClickListerner(object : CustomAdapter.OnItemLongClickListener {
            override fun onItemLongClickListener(
                view: View,
                position: Int,
                column: Int
            ) {
                if (column == 0) {
                    Toast.makeText(activity, "${dataset.path[position]}", Toast.LENGTH_SHORT).show()
                }

                if (column == 1 || column == 2) {
                    var fromPosition: Int = position
                    var toPosition: Int = 0
                    var positionStart = position
                    val step = 8 // ?????
                    //var range = toPosition - fromPosition
                    var valid : Boolean = false
                    if (column == 1 ){
                        toPosition = position - step
                        if(toPosition >= 0){
                            valid = true
                            positionStart = toPosition
                        }
                    }
                    else {
                        toPosition = position + step
                        if(toPosition < dataset.size){
                            valid = true
                            positionStart = fromPosition
                        }
                    }

                    if (valid) {
                        val title = dataset.title[fromPosition]
                        val path = dataset.path[fromPosition]
                        val selected = dataset.selected[fromPosition]
                        dataset.removeAt(fromPosition)
                        dataset.add(toPosition, title, path, selected)
                        adapter.notifyItemRangeChanged(positionStart,step+1)
                    }
                    else {
                        //Toast.makeText(activity, "OoB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }


    fun readFromUri(uri: Uri) {
        val inputStream = activity?.getContentResolver()?.openInputStream(uri)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        bufferedReader.forEachLine {
            if (it.isNotBlank()) {
                if (it != "#EXTM3U" && it != "#EXTINF:,") {
                    val title = it.replace("^.*/".toRegex(), "")
                    dataset.add(title, it, true)
                }
            }
        }
    }


    fun writeToUri(uri: Uri) {
        val outputStream: OutputStream? = activity?.getContentResolver()?.openOutputStream(uri, "w")
        val outputStreamBuffer = OutputStreamWriter(outputStream)
        val bufferedWriter = BufferedWriter(outputStreamBuffer)
        bufferedWriter.write("#EXTM3U")
        bufferedWriter.newLine()

        for (i in 0..dataset.size - 1) {
            if (dataset.selected[i]) {
                bufferedWriter.write("#EXTINF:,")
                bufferedWriter.newLine()
                bufferedWriter.write(dataset.path[i])
                bufferedWriter.newLine()
            }
        }
        bufferedWriter.close()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                if (uri != null) {
                    m3u8Uri = uri
                    readFromUri(m3u8Uri)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                if (uri != null) {
                    m3u8Uri = uri
                    writeToUri(m3u8Uri)
                    Toast.makeText(activity, "Successfully made new file", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (requestCode == WRITE_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity, "Canceled or Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
