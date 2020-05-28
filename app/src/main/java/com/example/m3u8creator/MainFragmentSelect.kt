package com.example.m3u8creator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main_read.*
import kotlinx.android.synthetic.main.activity_main_select.*
import java.io.*


class MainFragmentSelect :Fragment() {
    var dataset = Dataset()
    var adapter = CustomAdapter(dataset)
    val readRequestCode: Int = Constant.READ_REQUEST_CODE
    val writeRequestCode: Int = Constant.WRITE_REQUEST_CODE
    lateinit var m3u8Uri: Uri

    var overwriteString = "Overwrite "
    var selectedDefault = false

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

        var rapidLongClicked = false
        var rapidFromPosition = 0

        fun getFileList2() {
            //
            val mProjection : Array<String> = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
            )
            val textInputEditText = activity?.findViewById<TextInputEditText>(R.id.textInputEditText)
            var searchString = textInputEditText?.text.toString()
            lateinit var selectionArgs: Array<String>


            //Search Mode
            val selectedItemPosition = activity?.findViewById<Spinner>(R.id.spinnerSearch)?.selectedItemPosition
            if (searchString == null){

            }
            else{
                var selectionArg = "*" + searchString + "*"
                selectionArgs = arrayOf(selectionArg)
            }

            //Query
            var sortOrder : String?  = null
            var selectionClause: String? = null

            when (selectedItemPosition){
                Constant.SPINNER_PATH -> {
                    selectionClause = "${MediaStore.Audio.Media.DATA} GLOB ?"
                    sortOrder = MediaStore.Audio.Media.DATA
                }
                Constant.SPINNER_TITLE -> {
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
                            dataset.add(strTitle, strPath, selectedDefault, false)
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

/*
        dataset.add("A", "/storage/a", false, false) //dummy
        dataset.add("B", "/storage/b", false, false) //dummy
        dataset.add("C", "/storage/c", false, false) //dummy
        dataset.add("D", "/storage/d", false, false) //dummy
        dataset.add("E", "/storage/e", false, false) //dummy
        dataset.add("F", "/storage/f", false, false) //dummy
        dataset.add("G", "/storage/g", false, false) //dummy
        dataset.add("H", "/storage/h", false, false) //dummy
*/

        val layoutManager = LinearLayoutManager(activity)

        simpleRecyclerView.layoutManager = layoutManager
        simpleRecyclerView.adapter = adapter
        simpleRecyclerView.setHasFixedSize(false)




        //4 commands
        val buttonC = activity?.findViewById<Button>(R.id.buttonC)
        buttonC?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val spinnerSelect = activity?.findViewById<Spinner>(R.id.spinnerSelect)
                val selectedItemPosition = spinnerSelect?.selectedItemPosition

                if(dataset.rapidMode){
                    Toast.makeText(context, "Now in Rapid Sort Mode", Toast.LENGTH_SHORT).show()
                    return
                }

//                Toast.makeText(context, "selectedItemPosition = ${selectedItemPosition}", Toast.LENGTH_SHORT).show()

                when(selectedItemPosition) {
                    Constant.SPINNER_REDUCE -> {
                        dataset.removeUnused()
                    }
                    Constant.SPINNER_ALL -> {
                        dataset.selectAll(true)
                    }
                    Constant.SPINNER_NONE -> {
                        dataset.selectAll(false)
                    }
                    Constant.SPINNER_INVERT -> {
                        dataset.invertSelectionStatus()
                    }
                    Constant.SPINNER_RESET -> {
                        dataset.clear()
                        val textInputEditText = activity?.findViewById<TextInputEditText>(R.id.textInputEditText)
                        textInputEditText?.setText("")
                    }
                }
                adapter.notifyDataSetChanged()
                toastSelectStatus(dataset.count, dataset.size)
            }
        })
        buttonC?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {

                dataset.rapidMode = !dataset.rapidMode
                val tabLayout =activity?.container?.tabLayout


                if (dataset.rapidMode){
                    Toast.makeText(context, "Rapid Sort Mode", Toast.LENGTH_SHORT).show()
                    tabLayout?.setSelectedTabIndicatorColor(Color.BLUE)

                }
                else {
                    Toast.makeText(context, "Normal Mode", Toast.LENGTH_SHORT).show()
                    tabLayout?.setSelectedTabIndicatorColor(Color.GRAY)
                }
                adapter.notifyDataSetChanged()
                return true
            }
        })




        // search
        val buttonRL = activity?.findViewById<Button>(R.id.buttonRL)
        buttonRL?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                selectedDefault = false
                getFileList2()
            }
        })
        buttonRL?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                selectedDefault = true
                getFileList2()
                return true
            }
        })

        val buttonRF = activity?.findViewById<Button>(R.id.buttonR)
        buttonRF?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                selectedDefault = true
                openFileGui(readRequestCode)
            }
        })
        buttonRF?.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                selectedDefault = false
                openFileGui(readRequestCode)
                return true
            }
        })



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

                if (column == Constant.COLUMN_TV) {
                    dataset.invertSelectionStatus(position)
                    adapter.notifyItemChanged(position)
                }
                if (column == Constant.COLUMN_BUTTON_UP) {
                    if (position > 0) {
                        dataset.swap(position - 1, position)
                        adapter.notifyItemRangeChanged(position - 1, 2)
                    }
                }
                if (column == Constant.COLUMN_BUTTON_DOWN) {
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
                    if(!dataset.rapidMode) {
                        Toast.makeText(activity, "${dataset.path[position]}", Toast.LENGTH_SHORT).show()
                    }
                    else {

                        if (rapidLongClicked) {
 //                           Toast.makeText( activity, "2nd Tap (from ${rapidFromPosition} to ${position})", Toast.LENGTH_SHORT ).show()
                            Toast.makeText( activity, "2nd Long Tap", Toast.LENGTH_SHORT ).show()
                            dataset.invertSelectionStatus(rapidFromPosition, position)

                            if (position > rapidFromPosition){
                                val range = position - rapidFromPosition  +1
                                adapter.notifyItemRangeChanged(rapidFromPosition, range)
                            }
                            else {
                                val range = rapidFromPosition - position +1
                                adapter.notifyItemRangeChanged(position, range)
                            }
                        }
                        else{
//                            Toast.makeText(activity, "1st Tap (from ${position})", Toast.LENGTH_SHORT).show()
                            Toast.makeText(activity, "1st Long Tap", Toast.LENGTH_SHORT).show()
                            rapidFromPosition = position
                        }
                        rapidLongClicked = !rapidLongClicked
                    }
                }

                if (column == Constant.COLUMN_BUTTON_UP || column == Constant.COLUMN_BUTTON_DOWN) {

                    if(dataset.rapidMode && dataset.rapidModeCount > 1 && dataset.rapidSelected[position]){
                        if (column == Constant.COLUMN_BUTTON_UP) {
                            val nextPosition = dataset.concentrateUpward()
                            (simpleRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(nextPosition)
                        }
                        else{
                            val nextPosition = dataset.concentrateDownward()
                            (simpleRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(nextPosition)
                        }
                        adapter.notifyDataSetChanged()
                        Toast.makeText(activity, "Concentrated", Toast.LENGTH_SHORT).show()
                    }
                    //if (!dataset.rapidMode) {
                    else {
                        var fromPosition: Int = position
                        var toPosition: Int = 0
                        var positionStart = position
                        val step = Constant.STEP
                        var range = 0
                        if (column == Constant.COLUMN_BUTTON_UP) {
                            toPosition = position - step
                            range = step + 1
                            if (toPosition < 0) {
                                toPosition = 0
                                range = fromPosition + 1
                            }
                            positionStart = toPosition
                        } else {
                            toPosition = position + step
                            range = step + 1
                            if (toPosition >= dataset.size) {
                                toPosition = dataset.size - 1
                                range = toPosition - fromPosition + 1
                            }
                            positionStart = fromPosition
                        }

                        if (fromPosition != toPosition) {
                            //Toast.makeText(activity, "from ${fromPosition} to ${toPosition}", Toast.LENGTH_SHORT).show()
                            dataset.move(fromPosition, toPosition)
                            adapter.notifyItemRangeChanged(positionStart, range)
                        }
                    }

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
                    buttonRL?.setText(resources.getString(R.string.mm_button_RL))
                    buttonRF?.setText(resources.getString(R.string.mm_button_R))
                    buttonC?.setText(resources.getString(R.string.mm_button_C))
                    buttonWrite?.setText(resources.getString(R.string.mm_button_W))
                    buttonOverwrite?.setText(resources.getString(R.string.mm_button_OW))
                    overwriteString=resources.getString(R.string.mm_button_OW) + " "
                }
                else{
                    //Toast.makeText(context, "Monad Mode: OFF", Toast.LENGTH_SHORT).show()
                    buttonRL?.setText(resources.getString(R.string.button_RL))
                    buttonRF?.setText(resources.getString(R.string.button_R))
                    buttonC?.setText(resources.getString(R.string.button_C))
                    buttonWrite?.setText(resources.getString(R.string.button_W))
                    buttonOverwrite?.setText(resources.getString(R.string.button_OW))
                    overwriteString=resources.getString(R.string.button_OW) + " "
                }
                return true
            }
        })

    }


    fun readFromUri(uri: Uri, selectedDefault: Boolean) {
        val inputStream = activity?.getContentResolver()?.openInputStream(uri)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        bufferedReader.forEachLine {
            if (it.isNotBlank()) {
                if (it != "#EXTM3U" && it != "#EXTINF:,") {
                    //val title = it.replace("^.*/".toRegex(), "")
                    val title = basename(it)
                    dataset.add(title, it, selectedDefault, false)
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
                    readFromUri(m3u8Uri, selectedDefault)
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
