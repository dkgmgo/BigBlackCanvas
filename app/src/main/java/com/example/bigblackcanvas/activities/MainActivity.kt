package com.example.bigblackcanvas.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.example.bigblackcanvas.R
import com.example.bigblackcanvas.utils.InUtils
import com.example.bigblackcanvas.utils.OutUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : Activity() {

    private var todos = setOf<String>("TODO: \n", "Don't die, find love and make some dough")

    private lateinit var quoteView: TextView
    private lateinit var todoView: TextView
    private lateinit var author: TextView
    private lateinit var switch1: Switch
    private lateinit var appsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteView = findViewById(R.id.quote)
        todoView = findViewById(R.id.todo)
        author = findViewById(R.id.author)
        switch1 = findViewById(R.id.switch1)
        appsButton = findViewById(R.id.appsButton)

        init()

        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            CoroutineScope(Dispatchers.Main).launch {
                val quote = if (isChecked) OutUtils.getTodayQuote()  else OutUtils.getRandomQuote()
                quoteView.text = quote.quoteText
                author.text = quote.author
            }
        }
        switch1.isChecked = true

        todoView.movementMethod = ScrollingMovementMethod()

        appsButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, AppsListActivity::class.java)
            startActivity(intent)
        })

        appsButton.setOnLongClickListener(View.OnLongClickListener {
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            startActivity(intent)
            false
        })
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init(){
        //check to see if we use the default launcher
        if(!InUtils.isWorkingTime()){
            try {
                val i = InUtils.buildLaunchingIntent("com.sec.android.app.launcher", "com.sec.android.app.launcher.activities.LauncherActivity")
                startActivity(i)
            } catch (e: Exception) {
                println("Error: $e")
            }
        }

        //refresh todos
        todos = todos.plus(InUtils.getListFromShp(getSharedPreferences(InUtils.DATA, Context.MODE_PRIVATE), InUtils.TODOS))
        todoView.text = InUtils.displayTodos(todos.toList())
    }
}