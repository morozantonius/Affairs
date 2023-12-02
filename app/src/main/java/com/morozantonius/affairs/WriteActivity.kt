package com.morozantonius.affairs

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class WriteActivity : AppCompatActivity() {
    private lateinit var taskEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        taskEditText = findViewById(R.id.taskEditText)
        saveButton = findViewById(R.id.saveButton)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val colorStateList = ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.mtrl_tabs_colored_ripple_color))
        saveButton.backgroundTintList = colorStateList

        saveButton.setOnClickListener {
            val task = taskEditText.text.toString().trim()
            if (task.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(EXTRA_TASK, task)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter a affairs!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_TASK = "extra_task"
    }
}
