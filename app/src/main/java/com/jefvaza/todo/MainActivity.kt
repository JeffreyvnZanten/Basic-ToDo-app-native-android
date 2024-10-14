package com.jefvaza.todo;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = TodoDatabase.getDatabase(applicationContext).todoDao()
        viewModel = TodoViewModel(dao)
        enableEdgeToEdge()

        setContent {
            TodoApp(viewModel)
        }
    }
}