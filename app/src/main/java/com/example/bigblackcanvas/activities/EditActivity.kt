package com.example.bigblackcanvas.activities

import android.app.Activity
import android.content.Context
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.bigblackcanvas.R
import com.example.bigblackcanvas.utils.InUtils

class EditActivity : Activity(){
    private var packages: List<ResolveInfo> = ArrayList()
    private var packageNames = ArrayList<String>()
    private var appNames = ArrayList<String>()

    private lateinit var listView: ListView
    private lateinit var addTodo: EditText
    private lateinit var removeTodo: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonRemove: Button
    private lateinit var search: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        listView = findViewById(R.id.listView)
        addTodo = findViewById(R.id.add_todo)
        removeTodo = findViewById(R.id.remove_todo)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonRemove = findViewById(R.id.buttonRemove)
        search = findViewById(R.id.search_text)
        val shp = getSharedPreferences(InUtils.DATA, Context.MODE_PRIVATE)
        packages = InUtils.getInstalledApps(packageManager)
        search("")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appNames)

        listView.adapter = adapter
        listView.divider = null
        listView.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            InUtils.addEltIntoListInShp(shp, InUtils.APPS, appNames[position]+InUtils.APP_SEPARATOR+packageNames[position])
            Toast.makeText(this, "App Added", Toast.LENGTH_SHORT).show()
        }

        buttonAdd.setOnClickListener(View.OnClickListener {
            if(!addTodo.text.isNullOrEmpty()){
                InUtils.addEltIntoListInShp(shp, InUtils.TODOS, addTodo.text.toString())
                addTodo.text = null
                Toast.makeText(this, "Todo Added", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(intent)
            }
        })

        buttonRemove.setOnClickListener(View.OnClickListener {
            if(!removeTodo.text.isNullOrEmpty()){
                var index = Integer.parseInt(removeTodo.text.toString()) - 2 //we start at 0 and we keep the first
                index = if (index < 0) 0 else index
                InUtils.removeEltFromListInShpByIndex(shp, InUtils.TODOS, index)
                removeTodo.text = null
                Toast.makeText(this, "Todo Removed", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(intent)
            }
        })

        search.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                search(""+p0)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun search(text: String){
        packageNames.clear()
        appNames.clear()
        packages.forEach{
            val appName = it.loadLabel(packageManager) as String
            if(appName.lowercase().startsWith(text.lowercase())){
                packageNames.add(it.activityInfo.packageName)
                appNames.add(appName)
            }
        }
    }
}