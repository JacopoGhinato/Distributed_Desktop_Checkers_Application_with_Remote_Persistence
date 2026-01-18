//ALREDY OK, DO NOT CHANGE
package org.example.isel.tds.checkers.model

@JvmInline
value class Name(val value: String) {
    init { require( isValid(value) ) { "Invalid name $value" } }
    override fun toString() = value
    companion object {
        fun isValid(value: String) =
            value.isNotBlank() && value.all { it.isLetterOrDigit() }
    }
}