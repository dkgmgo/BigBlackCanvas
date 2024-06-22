package com.example.bigblackcanvas.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.bigblackcanvas.R
import com.example.bigblackcanvas.utils.InUtils

class AppsListActivity: Activity() {

    private lateinit var listView: ListView
    private lateinit var packages: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        listView = findViewById(R.id.listView)
        val shp = getSharedPreferences(InUtils.DATA, Context.MODE_PRIVATE)
        val p = listView.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(100, 0, 0, 0)
        packages = InUtils.getListFromShp(shp, InUtils.APPS)
        val packageNames = ArrayList<String>()
        val appNames = ArrayList<String>()
        packages.forEach{
            val strTab = it.split(InUtils.APP_SEPARATOR)
            appNames.add(strTab[0])
            if (strTab.size >= 2) packageNames.add(strTab[1]) else packageNames.add("")
        }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appNames)
        listView.divider = null
        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _ ->
            try {
                startActivity(packageManager.getLaunchIntentForPackage(packageNames[position]))
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener{_, _, position, _ ->
            InUtils.removeEltFromListInShp(shp, InUtils.APPS, packages[position])
            Toast.makeText(this, "App Removed", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(intent)
            false
        }
    }
}