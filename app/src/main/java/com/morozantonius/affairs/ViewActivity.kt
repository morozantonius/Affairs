package com.morozantonius.affairs

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewActivity : AppCompatActivity() {
    private lateinit var addButton: Button
    private lateinit var tasks: MutableList<String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    companion object {
        const val REQUEST_CODE_ADD_TASK = 1
        const val EXTRA_TASK = "extra_task"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        addButton = findViewById(R.id.addButton)
        tasks = mutableListOf()
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)

        recyclerView = findViewById(R.id.todoRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasks)
        recyclerView.adapter = adapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val paint = Paint()
            private var currentSwipedPosition: Int = -1

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                currentSwipedPosition = viewHolder.adapterPosition
                val position = currentSwipedPosition
                val removedTask = tasks.removeAt(position)
                adapter.notifyDataSetChanged()
                saveTasks()

                val toast = Toast.makeText(
                    this@ViewActivity,
                    "Case deleted!",
                    Toast.LENGTH_LONG
                )

                val toastView = toast.view
                toastView?.setBackgroundColor(Color.BLACK)

                val text = toastView?.findViewById<TextView>(android.R.id.message)
                text?.setTextColor(Color.RED)
                text?.setTextAppearance(android.R.style.TextAppearance)

                toast.setGravity(Gravity.TOP, 0, 50)
                toast.show()

                resetSwipeBackground(viewHolder.itemView)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                paint.color = Color.RED
                c.drawRect(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat(),
                    paint
                )

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                c?.drawRect(left, top, right, bottom, paint)
            }

            private fun resetSwipeBackground(view: View) {
                view.postDelayed({
                    try {
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(currentSwipedPosition)
                        viewHolder?.itemView?.setBackgroundColor(Color.TRANSPARENT)
                        currentSwipedPosition = -1
                    } catch (e: Exception) {
                        Log.e("ViewActivity", "Exception in resetSwipeBackground", e)
                    }
                }, 0)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addButton.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
        }

        loadTasks()
    }

    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("tasks", tasks.toSet())
        editor.apply()
    }

    private fun loadTasks() {
        val savedTasks = sharedPreferences.getStringSet("tasks", setOf())
        tasks.clear()
        if (savedTasks != null) {
            tasks.addAll(savedTasks)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == Activity.RESULT_OK) {
            val task = data?.getStringExtra(EXTRA_TASK)
            task?.let {
                tasks.add(it)
                adapter.notifyDataSetChanged()
                saveTasks()
            }
        }
    }
}
