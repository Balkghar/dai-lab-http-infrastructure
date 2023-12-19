package ch.heig.dai.lab.http.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.bson.Document;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.path;

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
        path("api", () -> {
            app.get("/notes/{id}", this::getNote);
            app.get("/notes", this::getAllNotes);
            app.post("/notes", this::createNote);
            app.put("/notes/{id}", this::updateNote);
            app.delete("/notes/{id}", this::deleteNote);
        });
    }

    /**
     * Create a new note.
     *
     * @param ctx The Javalin context.
     */
    public void createNote(Context ctx) {
        Note note;
        try {
            note = ctx.bodyAsClass(Note.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Invalid note format");
            return;
        }

        Document createdNote = notesService.createNote(note);
        if (createdNote == null) {
            ctx.status(500);
            ctx.result("Note creation failed");
            return;
        }

        ctx.status(201);
        ctx.json(createdNote);
    }

    /**
     * Get a note by its ID.
     *
     * @param ctx The Javalin context.
     */
    public void getNote(Context ctx) {
        String id = ctx.pathParam("id");
        final Document foundNote = notesService.getNote(id);
        if (foundNote != null) {
            ctx.status(200);
            ctx.json(foundNote.toJson());
        } else {
            ctx.status(404);
            ctx.result("Note not found");
        }
    }

    /**
     * Get all notes.
     *
     * @param ctx The Javalin context.
     */
    public void getAllNotes(Context ctx) {
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
    public void updateNote(Context ctx) {
        String noteId = ctx.pathParam("id");
        Note note = ctx.bodyAsClass(Note.class);

        Document updatedDocument = notesService.updateNote(noteId, note);
        if (updatedDocument != null) {
            ctx.status(200);
            ctx.json(updatedDocument);
        } else {
            ctx.status(500);
            ctx.result("Note update failed");
        }
    }

    /**
     * Delete a note.
     *
     * @param ctx The Javalin context.
     */
    public void deleteNote(Context ctx) {
        String id = ctx.pathParam("id");
        Document deletedNote = notesService.deleteNote(id);
        if (deletedNote != null) {
            ctx.status(200);
            ctx.json(deletedNote);
        } else {
            ctx.status(404);
            ctx.result("Note not found");
        }
    }

}