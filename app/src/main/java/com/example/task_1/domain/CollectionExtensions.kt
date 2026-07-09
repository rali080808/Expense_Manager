package com.example.task_1.domain

fun List<Category>.getById(id: Long?) : Category? {
    return this.find{it.id == id}
}
fun List<Category>.containsID(categoryID: Long): Boolean {
    if ( this.getById(categoryID) == null ) return false;
    return true;
}

