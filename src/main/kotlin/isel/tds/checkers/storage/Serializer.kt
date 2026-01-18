package isel.tds.checkers.isel.tds.checkers.storage

interface Serializer<T> {
    fun serialize(data: T): String
    fun deserialize(data: String): T
}
