package com.example.noobtodo

import android.graphics.drawable.InsetDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                // Remove item from list
                listOfTasks.removeAt(position)
                // Notify the adapter that data set has changed
                adapter.notifyItemRemoved(position)
                // Save items
                saveItems()
            }

        }

        // Example tasks
        // listOfTasks.add("Task 1")
        // listOfTasks.add("Task 2")

        // Load items instead of populating with above example tasks
        loadItems()

        // Look up recyclerView in layout
        val taskList = findViewById<RecyclerView>(R.id.taskList)
        // Tasks are static and won't change so we can do the following for smoother scrolling
        taskList.setHasFixedSize(true)
        // Create adapter passing in the sample user data
        adapter = TaskItemAdapter(listOfTasks, onLongClickListener)
        // Attach the adapter to taskList to populate items
        taskList.adapter = adapter
        // Set layout manager to position the items
        taskList.layoutManager = LinearLayoutManager(this)
        // Custom divider item decoration
        var a = obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        var div = a.getDrawable(0)
        val marg = resources.getDimensionPixelSize(R.dimen.dividerMargin)
        div = InsetDrawable(div, marg, 0, marg, 0)
        a.recycle()
        val itemDec = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDec.setDrawable(div)
        // Set item decoration
        taskList.addItemDecoration(itemDec)

        // Detect when user clicks on add button
        findViewById<Button>(R.id.button).setOnClickListener {
            // Want to add task to recyclerview when clicked on if the addTaskField is populated
            Log.i("Astem","User clicked on button")
            val textField = findViewById<EditText>(R.id.addTaskField)
            val taskName = textField.text.toString()
            if (!taskName.isNullOrBlank()) {
                listOfTasks.add(taskName)
                // Notify adapter that our data has been updated
                adapter.notifyItemInserted(listOfTasks.size - 1)
                // Scroll to newest task
                taskList.scrollToPosition(adapter.itemCount - 1)
                // Save items
                saveItems()
            }
            // Reset text field (don't know if we want to reset text field regardless)
            textField.setText("")
        }
    }

    // Save the data that the user has inputted
    // Save data by writing and reading from a file

    // Get the file we need
    private fun getDataFile() : File {

        // Every line is going to represent a specific task in our list of tasks
        return File(filesDir, "data.txt")
    }

    // Load the items by reading every line in the data file
    private fun loadItems() {
        try {
            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException : IOException) {
            ioException.printStackTrace()
        }
    }

    // Save items by writing the into our data file
    private fun saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listOfTasks)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}