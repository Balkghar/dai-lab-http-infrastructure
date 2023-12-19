package ch.heig.dai.lab.http.api;

import io.javalin.http.Context;
import org.bson.Document;
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

        // Create mock notes as Documents
        Document doc1 = new Document("title", "title1").append("content", "content1");
        Document doc2 = new Document("title", "title2").append("content", "content2");

        // Mock the behavior of notesService.createNote()
//        when(notesService.createNote(any(Note.class))).thenAnswer(invocation -> {
//            Note note = invocation.getArgument(0);
//            if (note.title().equals("title1") && note.content().equals("content1")) {
//                return doc1;
//            } else if (note.title().equals("title2") && note.content().equals("content2")) {
//                return doc2;
//            } else {
//                return null;
//            }
//        });
    }

    @Test
    public void getAllNotes() {
        // Arrange
        List<Document> expectedDocs = Arrays.asList(
            new Document("title", "title1").append("content", "content1"),
            new Document("title", "title2").append("content", "content2")
        );
        when(notesService.getAllNotes()).thenReturn(expectedDocs);

        // Act
        noteRoutes.getAllNotes(ctx);

        // Assert
        verify(notesService).getAllNotes();
        verify(ctx).json(expectedDocs);
    }

    @Test
    public void getNote() {
        // Arrange
        int id = 1;
        Document expectedDoc = new Document("title", "title1").append("content", "content1");
        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
        when(notesService.getNote(id)).thenReturn(expectedDoc);

        // Act
        noteRoutes.getNote(ctx);

        // Assert
        verify(notesService).getNoteById(id);
        verify(ctx).json(expectedDoc);
    }

    @Test
    public void createNote() {
        // Arrange


        // Act

        // Assert
    }


    @Test
    public void updateNote() {

    }

    @Test
    public void deleteNote() {

    }

//    @Test
//    public void testGetNote() {
//        // Arrange
//        int id = 1;
//        Note note = new Note(); // Create a sample note with id 1
//        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
//        when(notesService.getNoteById(id)).thenReturn(note);
//
//        // Act
//        // Call your route handler here, e.g., noteRoutes.getNote(ctx);
//
//        // Assert
//        verify(notesService).getNoteById(id);
//        verify(ctx).json(note);
//    }
}
