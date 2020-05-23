package com.example.m3u8creator


import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


//


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container.adapter = TabAdapter(supportFragmentManager, this)
        tabLayout.setupWithViewPager(container)

        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun requestWritePermission() {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1024)
            } else {
                Toast.makeText(applicationContext, "R/W", Toast.LENGTH_SHORT)
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1024)
            }
        }

        fun checkPermission() {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                //textView.append("Write Denied\n")
                requestWritePermission()
            }
        }

        /*
        fun replaceFragment(fragment: Fragment) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.commit()
        }
         */

        checkPermission()
//        val sto = isExternalStorageWritable() //as boolean;
//        if (!sto) {
//            Toast.makeText (applicationContext, "STORAGE UNAVAILABLE", Toast.LENGTH_LONG).show()
//        }
    }
}
