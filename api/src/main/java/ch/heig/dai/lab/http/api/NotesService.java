package ch.heig.dai.lab.http.api;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

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
        Document doc = new Document("title", note.title()).append("content", note.content());
        notesCollection.insertOne(doc);
        return doc;
    }

    public Document getNote(String id) {
        return notesCollection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    public List<Document> getAllNotes() {
        List<Document> notes = new ArrayList<>();
        for (Document note : notesCollection.find()) {
            notes.add(note);
        }
        return notes;
    }

    public Document updateNote(String id, Note note) {
        // Add logic to update a note
        return null;
    }

    public Document deleteNote(String id) {
        // Add logic to delete a note
        return null;
    }
}
