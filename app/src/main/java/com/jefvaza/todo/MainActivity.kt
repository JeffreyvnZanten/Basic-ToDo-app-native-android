package com.jefvaza.todo;

import Todo
import TodoDao
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.util.Log
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        lifecycleScope.launch {
            try {
                val dao = TodoDatabase.getDatabase(applicationContext).todoDao()
                Log.d("MainActivity", "Database initialized")

                viewModel = TodoViewModel(dao)
                Log.d("MainActivity", "ViewModel created")

                setContent {
                    Log.d("MainActivity", "setContent called")
                    TodoApp(viewModel)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in onCreate", e)
            }
        }
    }
}

@Composable
fun TodoApp(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTodo("New Todo")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(todos) { todo ->
                Text(text = todo.task, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

class TodoViewModel(private val dao: TodoDao) : ViewModel() {
    val todos: StateFlow<List<Todo>> = dao.getAllTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTodo(task: String) {
        viewModelScope.launch {
            dao.insertTodo(Todo(task = task))
        }
    }
}

class TodoViewModelFactory(private val dao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}