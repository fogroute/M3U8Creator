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
import kotlinx.android.synthetic.main.activity_main_read.*
import kotlinx.android.synthetic.main.activity_main_select.*
import java.io.*


class MainFragmentSelect :Fragment() {

    var dataset = Dataset()
    var adapter = CustomAdapter(dataset)
    val readRequestCode: Int = Constant.READ_REQUEST_CODE
    val writeRequestCode: Int = Constant.WRITE_REQUEST_CODE
    lateinit var m3u8Uri: Uri

    val searchAll : Int = Constant.SEARCH_ALL
    val searchTitle : Int = Constant.SEARCH_TITLE
    val searchPath : Int = Constant.SEARCH_PATH

    var overwriteString = "Overwrite "

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
                searchAll -> {
                    selectionClause = null
                    sortOrder = null
                }
                searchPath -> {
                    selectionClause = "${MediaStore.Audio.Media.DATA} GLOB ?"
                    sortOrder = MediaStore.Audio.Media.DATA
                }
                searchTitle -> {
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
                    toastFileCount(cursor.count)
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
            if (requestCode == readRequestCode) {
                val intent = Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, readRequestCode)
            }
            if (requestCode == writeRequestCode) {
                val intent = Intent(
                    Intent.ACTION_CREATE_DOCUMENT
                ).apply {
//                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, writeRequestCode)
            }

        }


//        dataset.add("sharp", "/storage/1s", false) //dummy
//        dataset.add("principal", "/storage/2p", false) //dummy
//        dataset.add("diffuse", "/storage/3d", false) //dummy
//        dataset.add("fundamental", "/storage/4f", false) //dummy


        val layoutManager = LinearLayoutManager(activity)

        simpleRecyclerView.layoutManager = layoutManager
        simpleRecyclerView.adapter = adapter
        simpleRecyclerView.setHasFixedSize(false)




        //4 commands
        val buttonC = activity?.findViewById<Button>(R.id.buttonC)
        buttonC?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.removeUnused()
                adapter.notifyDataSetChanged()
                toastSelectStatus(dataset.count, dataset.size)
            }
        })

        val buttonClear =
            activity?.findViewById<Button>(R.id.buttonReset)  // view.findViewById<Button>(R.id.button)
        buttonClear?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.clear()
                adapter.notifyDataSetChanged()
                val textInputEditText = activity?.findViewById<TextInputEditText>(R.id.textInputEditText)
                textInputEditText?.setText("")
                toastSelectStatus(dataset.count, dataset.size)
            }
        })

        val buttonAll = activity?.findViewById<Button>(R.id.buttonAll)
        buttonAll?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.selectAll(true)
                adapter.notifyDataSetChanged()
                toastSelectStatus(dataset.count, dataset.size)
            }
        })
        buttonAll?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                dataset.invertSelectionStatus()
                adapter.notifyDataSetChanged()
                toastSelectStatus(dataset.count, dataset.size)
                return true
            }
        })

        val buttonZero =
            activity?.findViewById<Button>(R.id.buttonZero)  // view.findViewById<Button>(R.id.button)
        buttonZero?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dataset.selectAll(false)
                adapter.notifyDataSetChanged()
                toastSelectStatus(dataset.count, dataset.size)
            }
        })



        val buttonRL = activity?.findViewById<Button>(R.id.buttonRL)
        buttonRL?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
//                Toast.makeText(activity, "TO BE UPDATED", Toast.LENGTH_SHORT).show()
                getFileList2(searchPath)
            }
        })
        buttonRL?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                getFileList2(searchTitle)
                return true
            }
        })

        val buttonRF = activity?.findViewById<Button>(R.id.buttonR)
        buttonRF?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openFileGui(readRequestCode)
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
        val buttonWrite =
            activity?.findViewById<Button>(R.id.buttonW)
        buttonWrite?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openFileGui(writeRequestCode)
            }
        })

        val buttonOverwrite =
            activity?.findViewById<Button>(R.id.buttonOW)
        buttonOverwrite?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(activity, "Long Tap Required", Toast.LENGTH_SHORT).show()
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
                    dataset.count = if (selected) dataset.count+1 else dataset.count-1
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
                if (column == Constant.COLUMN_TV) {
                    Toast.makeText(activity, "${dataset.path[position]}", Toast.LENGTH_SHORT).show()
                }

                if (column == Constant.COLUMN_BOTTON_UP || column == Constant.COLUMN_BOTTON_DOWN) {
                    var fromPosition: Int = position
                    var toPosition: Int = 0
                    var positionStart = position
                    val step = 8 // ?????
                    //var range = toPosition - fromPosition
                    var valid  = false
                    if (column == Constant.COLUMN_BOTTON_UP ){
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
                    //else {
                        //Toast.makeText(activity, "OoB", Toast.LENGTH_SHORT).show()
                    //}
                }
            }
        })

        // JOKE
        var monadMode = false
        buttonWrite?.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                monadMode = !monadMode
                if (monadMode){
                    //Toast.makeText(context, "Monad Mode: ON", Toast.LENGTH_SHORT).show()
                    buttonRL?.setText("探")
                    buttonRF?.setText("読")
                    buttonAll?.setText("選")
                    buttonZero?.setText("解")
                    buttonC?.setText("減")
                    buttonClear?.setText("消")
                    buttonWrite?.setText("創")
                    buttonOverwrite?.setText("書")
                    overwriteString="書 "
                }
                else{
                    //Toast.makeText(context, "Monad Mode: OFF", Toast.LENGTH_SHORT).show()
                    buttonRL?.setText("Search")
                    buttonRF?.setText("Read File")
                    buttonAll?.setText("All")
                    buttonZero?.setText("None")
                    buttonC?.setText("Select")
                    buttonClear?.setText("Reset")
                    buttonWrite?.setText("New File")
                    buttonOverwrite?.setText("Overwrite")
                    overwriteString = "Overwrite "
                }
                return true
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
                    //val title = it.replace("^.*/".toRegex(), "")
                    val title = basename(it)
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

        val button = activity?.findViewById<Button>(R.id.buttonOW)

        if (requestCode == readRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                if (uri != null) {
                    val size0 = dataset.size
                    m3u8Uri = uri
                    readFromUri(m3u8Uri)
                    adapter.notifyDataSetChanged()
                    val sizeDiff = dataset.size - size0
                    toastFileCount(sizeDiff)

                    val fileName = basename(uri.path.toString())
                    button?.setText(overwriteString+"${fileName}")
                }
            }
        }

        if (requestCode == writeRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                if (uri != null) {
                    m3u8Uri = uri
                    writeToUri(m3u8Uri)
                    Toast.makeText(activity, "Successfully made new file", Toast.LENGTH_SHORT).show()

                    val fileName = basename(uri.path.toString())
                    button?.setText(overwriteString+"${fileName}")
                }
            }
        }
        if (requestCode == writeRequestCode && resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity, "Canceled or Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toastFileCount(count: Int){
        if (count < 2) {
            Toast.makeText(activity, "${count} File", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(activity, "${count} Files", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toastSelectStatus(count: Int, size: Int){
        Toast.makeText(activity, "${count}/${size}", Toast.LENGTH_SHORT).show()
    }
}

fun basename(path: String): String{
    val base = path.replace("^.*/".toRegex(), "")
    return base
}
