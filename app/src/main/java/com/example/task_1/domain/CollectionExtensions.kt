package com.example.task_1.domain

fun List<Category>.getById(id: Long?) : Category {
    return this.find{it.id == id} ?: Category()
}
fun List<Transaction>.getById(id: Long?) : Transaction? {
    return this.find{it.id == id}
}
fun List<Category>.containsID(categoryID: Long): Boolean {
    if ( this.getById(categoryID) == Category() ) return false
    return true
}

