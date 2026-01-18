//MongoStorgae.kt
package org.example.isel.tds.checkers.storage

import com.mongodb.MongoWriteException
import isel.tds.checkers.isel.tds.checkers.storage.Serializer
import isel.tds.checkers.isel.tds.checkers.storage.Storage

class MongoStorage<Key, Data>(
    collectionName: String,
    driver: MongoDriver,
    private val serializer: Serializer<Data>
) : Storage<Key, Data> {

    data class Doc(val _id: String, val data: String)

    private fun Doc(key: Key, data: Data) = Doc(key.toString(), serializer.serialize(data))

    val docs = driver.getCollection<Doc>(collectionName)

    override fun create(key: Key, data: Data) {
        try {
            docs.insertDocument(Doc(key, data))
        } catch (e: MongoWriteException) {
            error("Document $key already exists")
        }
    }

    override fun read(key: Key): Data? =
        docs.getDocument(key.toString())?.let {
            serializer.deserialize(it.data)
        }

    override fun update(key: Key, data: Data) {
        if (docs.getDocument(key.toString()) == null) {
            throw IllegalStateException("Document $key does not exists, impossible to update!")
        }
        docs.replaceDocument(key.toString(), Doc(key, data))
    }

    override fun delete(key: Key) {
        check(docs.deleteDocument(key.toString()))
        { "Document $key does not exist to delete" }
    }
}
