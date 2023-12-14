package ch.heig.dai.lab.http.api;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class NotesService {
    private final MongoCollection<Document> notesCollection;

    public NotesService() {
        MongoDatabase database = MongoDbConnection.getDatabase();
        notesCollection = database.getCollection("notes");
    }

    /**
     * Create a new note.
     *
     * @param note The note to create.
     * @return The created note.
     */
    public Document createNote(Note note) {
        Document doc = new Document("title", note.title())
                       .append("content", note.content());
        notesCollection.insertOne(doc);
        return doc;
    }

    public Document getNote(String id) {
        // Add logic to retrieve a note by id
        return null;
    }

    public Document updateNote(String id, Note note) {
        // Add logic to update a note
        return null;
    }

    public Document deleteNote(String id) {
        // Add logic to delete a note
        return null;
    }

    public Document getAllNotes() {
        // Add logic to retrieve all notes
        return null;
    }

    public Document getNotesByTitle(String title) {
        // Add logic to retrieve notes by title
        return null;
    }
}
