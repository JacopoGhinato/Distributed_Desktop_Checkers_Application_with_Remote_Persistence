//TextFileStorage
package org.example.isel.tds.checkers.storage

import isel.tds.checkers.isel.tds.checkers.storage.Serializer
import isel.tds.checkers.isel.tds.checkers.storage.Storage
import java.nio.file.Path
import kotlin.io.path.*

class TextFileStorage<Key, Data>( // Generic class to handle storing and reading from text files
    baseFolderName: String,
    private val serializer: Serializer<Data> // Serializer to convert Data objects to and from text
) : Storage<Key, Data> {

    // The base path where the files are stored
    private val basePath = Path(baseFolderName).toAbsolutePath()

    init {
        with(basePath) {
            if (!exists()) { //if the directory doesn't exist
                println("Creation of the directory $baseFolderName")  // For debug
                createDirectory()  // Create the directory if it doesn't exist
            } else check(isDirectory()) { "$name is not a directory" }  // Check if it's a valid directory
        }
    }

    // Helper function to handle file paths for reading/writing
    private inline fun <R> with(key: Key, fx: Path.()->R): R =
        (basePath / "$key.txt").fx()

    // Create a new file for storing data if it doesn't already exist
    override fun create(key: Key, data: Data) = with(key) {
        if (!exists()) {
            //println("Creation of text file: $key")  // Debug message
            val serializedData = serializer.serialize(data)  // Serialize the data object
            writeText(serializedData)  // Write the serialized data to the file
            //println("File $key creato e popolato con i dati iniziali.") //used for debug
        } else {
            // If file already exists, just print a message (no exception)
            println("The File $key alredy exist, no creation necessary.")
        }
    }

    // Read the data from a file, returning null if it doesn't exist or an error occurs
    override fun read(key: Key): Data? = with(key) {
        try {
            val content = readText()  // Read the content of the file
            // println("Contenuto del file $key.txt: $content")  // Debug print of file content
            return serializer.deserialize(content)  // Deserialize the content back into the Data object
        } catch (e: NoSuchFileException) {
            println("File $key.txt not found.")  // If the file doesn't exist, print an error message
            return null  // Return null if file is missing
        } catch (e: Exception) {
            //println("Error during reading the file $key.txt: ${e.message}")  // Catch other exceptions
            return null
        }
    }

    // Update an existing file with new data
    override fun update(key: Key, data: Data) = with(key) {
        check(exists()) { "File $key does not exist" }  // Ensure the file exists
        writeText(serializer.serialize(data))  // Serialize and overwrite the file with new data
    }

    // Delete a file
    override fun delete(key: Key) = with(key) {
        check(deleteIfExists()) { "File $key does not exist" }  // Delete the file if it exists
    }
}

