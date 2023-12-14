package ch.heig.dai.lab.http.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.bson.Document;

import java.util.List;

/**
 * Routes for the notes API.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class NoteRoutes {
    /**
     * The notes service to use.
     */
    private final NotesService notesService;

    /**
     * Constructor.
     *
     * @param notesService The notes service to use.
     */
    public NoteRoutes(NotesService notesService) {
        this.notesService = notesService;
    }

    /**
     * Register the routes for the notes API.
     *
     * @param app The Javalin app to register the routes with.
     */
    public void registerRoutes(Javalin app) {
        app.get("/notes/{id}", this::getNote);
        app.get("/notes", this::getAllNotes);
        app.post("/notes", this::createNote);
        app.put("/notes/{id}", this::updateNote);
        app.patch("/notes/{id}", this::partialUpdateNote);
        app.delete("/notes/{id}", this::deleteNote);
        app.get("/notes/search/{title}", this::searchNotes);
    }

    /**
     * Create a new note.
     *
     * @param ctx The Javalin context.
     */
    private void createNote(Context ctx) {
        Note note = ctx.bodyAsClass(Note.class);
        Document createdNote = notesService.createNote(note);
        ctx.json(createdNote);
    }

    /**
     * Get a note by its ID.
     *
     * @param ctx The Javalin context.
     */
    private void getNote(Context ctx) {
        String id = ctx.pathParam("id");
        final Document foundNote = notesService.getNote(id);
        if (foundNote != null) {
            ctx.json(foundNote.toJson());
        } else {
            ctx.status(404).result("Note not found");
        }
    }

    /**
     * Get all notes.
     *
     * @param ctx The Javalin context.
     */
    private void getAllNotes(Context ctx) {
        List<Document> allNotes = notesService.getAllNotes();
        if (allNotes == null || allNotes.isEmpty()) {
            ctx.status(404).result("No notes found");
            return;
        }
        ctx.json(allNotes);
    }

    /**
     * Update a note.
     *
     * @param ctx The Javalin context.
     */
    private void updateNote(Context ctx) {
        String id = ctx.pathParam("id");
        Note note = ctx.bodyAsClass(Note.class);
        Document updatedNote = notesService.updateNote(id, note);
        ctx.json(updatedNote);
    }

    /**
     * Partially update a note.
     *
     * @param ctx The Javalin context.
     */
    private void partialUpdateNote(Context ctx) {
        String id = ctx.pathParam("id");
        Note note = ctx.bodyAsClass(Note.class);
//        Document updatedNote = notesService.partialUpdateNote(id, note);
//        ctx.json(updatedNote);
    }

    /**
     * Delete a note.
     *
     * @param ctx The Javalin context.
     */
    private void deleteNote(Context ctx) {
        String id = ctx.pathParam("id");
        Document deletedNote = notesService.deleteNote(id);
        ctx.json(deletedNote);
    }

    /**
     * Search notes by title.
     *
     * @param ctx The Javalin context.
     */
    private void searchNotes(Context ctx) {
        String title = ctx.pathParam("title");
//        Document foundNotes = notesService.searchNotes(title);
//        ctx.json(foundNotes);
    }
}