package com.jefvaza.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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