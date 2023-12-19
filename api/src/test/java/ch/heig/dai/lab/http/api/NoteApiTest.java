package ch.heig.dai.lab.http.api;

import io.javalin.http.Context;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the note CRUD operations.
 */
public class NoteApiTest {
    private NotesService notesService;
    private NoteRoutes noteRoutes;
    private Context ctx;

    /**
     * Set up the mocks.
     */
    @BeforeEach
    public void setUp() {
        notesService = mock(NotesService.class);
        noteRoutes = new NoteRoutes(notesService); // Use the real NoteRoutes with the mocked service
        ctx = mock(Context.class);
    }

    @Test
    public void createNote() {
        // Arrange
        Note testNote = new Note(); // Properly initialized Note object
        Document createdNote = new Document();

        when(ctx.bodyAsClass(Note.class)).thenReturn(testNote);
        when(notesService.createNote(testNote)).thenReturn(createdNote);

        // Act
        noteRoutes.createNote(ctx);

        // Assert
        verify(notesService).createNote(testNote);
        verify(ctx).status(201);
        verify(ctx).json(createdNote);
    }

    // FIXME: extend to test when no notes are present
    @Test
    public void getAllNotes() {
        // Arrange
        List<Document> expectedDocs = Arrays.asList(new Document("title", "title1").append("content", "content1"), new Document("title", "title2").append("content", "content2"));
        when(notesService.getAllNotes()).thenReturn(expectedDocs);

        // Act
        noteRoutes.getAllNotes(ctx);

        // Assert
        verify(notesService).getAllNotes();
        verify(ctx).json(expectedDocs);
        verify(ctx).status(200);
    }

    @Test
    public void getNote() {
        // Arrange
        String id = new ObjectId().toString(); // Generate a random ObjectId
        String expectedNote = "{\"_id\": {\"$oid\": \"" + id + "\"}, \"title\": \"title1\", \"content\": \"content1\"}";
        when(ctx.pathParam("id")).thenReturn(id);
        when(notesService.getNoteById(id)).thenReturn(Document.parse(expectedNote));

        // Act
        noteRoutes.getNoteById(ctx);

        // Assert
        verify(notesService).getNoteById(id);
        verify(ctx).json(expectedNote);
        verify(ctx).status(200);
    }

    @Test
    public void updateNote() {
        // Arrange
        String noteId = "123"; // Example note ID
        Note updatedNote = new Note("updatedTitle", "updatedContent", null, null); // Assume Note has this constructor
        Document updatedDocument = new Document();

        when(ctx.pathParam("id")).thenReturn(noteId);
        when(ctx.bodyAsClass(Note.class)).thenReturn(updatedNote);
        when(notesService.updateNote(noteId, updatedNote)).thenReturn(updatedDocument);

        // Act
        noteRoutes.updateNote(ctx);

        // Assert
        verify(notesService).updateNote(noteId, updatedNote);
        verify(ctx).status(200); // Verify that status 200 is set
        verify(ctx).json(updatedDocument); // Verify that the response is JSON
    }

    @Test
    public void deleteNote_SuccessfulDeletion() {
        // Arrange
        String noteId = "123";
        Document deletedNote = new Document(); // Assuming Document is correctly instantiated
        when(ctx.pathParam("id")).thenReturn(noteId);
        when(notesService.deleteNote(noteId)).thenReturn(deletedNote);

        // Act
        noteRoutes.deleteNote(ctx);

        // Assert
        verify(notesService).deleteNote(noteId);
        verify(ctx).status(200);
        verify(ctx).json(deletedNote);
    }

    @Test
    public void deleteNote_NoteNotFound() {
        // Arrange
        String noteId = "nonExistingId";
        when(ctx.pathParam("id")).thenReturn(noteId);
        when(notesService.deleteNote(noteId)).thenReturn(null);

        // Act
        noteRoutes.deleteNote(ctx);

        // Assert
        verify(notesService).deleteNote(noteId);
        verify(ctx).status(404);
        verify(ctx).result("Note not found");
    }
}
