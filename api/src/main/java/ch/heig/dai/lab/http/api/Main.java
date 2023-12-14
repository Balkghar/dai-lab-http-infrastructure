package ch.heig.dai.lab.http.api;

import io.javalin.Javalin;
import org.bson.Document;

/**
 * Main class for the Javalin server.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Main {
    /**
     * Entry point for the Javalin server.
     *
     * @param args Command line arguments, not used.
     */
    public static void main(String[] args) {
        final NotesService notesService = new NotesService();
        Javalin app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        }).start(7000);

        // Enable CORS for all requests
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        });

        setupRoutes(app, notesService);
    }

    /**
     * Set up the routes for the Javalin server.
     *
     * @param app          The Javalin app.
     * @param notesService The notes service.
     */
    private static void setupRoutes(Javalin app, NotesService notesService) {
        // Get all notes
        app.get("/notes", ctx -> {
            final Document allNotes = notesService.getAllNotes();
            if (allNotes == null) {
                ctx.status(404).result("No notes found");
                return;
            }
            ctx.json(allNotes.toJson());
        });

        // Get a note by id
        app.get("/notes/{id}", ctx -> {
            String id = ctx.pathParam("id");
            final Document foundNote = notesService.getNote(id);
            if (foundNote != null) {
                ctx.json(foundNote.toJson());
            } else {
                ctx.status(404).result("Note not found");
            }
        });

        // Create a note
        app.post("/notes", ctx -> {
            final Note note = ctx.bodyAsClass(Note.class);
            // FIXME: validate note
            final Document createdNote = notesService.createNote(note);
            ctx.json(createdNote.toJson());
        });

        // Update a note
        app.put("/notes/{id}", ctx -> {
            final String id = ctx.pathParam("id");
            final Note note = ctx.bodyAsClass(Note.class);
            // FIXME: validate note
            final Document updatedNote = notesService.updateNote(id, note);
            if (updatedNote != null) {
                ctx.json(updatedNote.toJson());
            } else {
                ctx.status(404).result("Note not found");
            }
        });

        // Partially update a note
        app.patch("/notes/{id}", ctx -> {
            // TODO
        });

        // Delete a note
        app.delete("/notes/{id}", ctx -> {
            final String id = ctx.pathParam("id");
            final Document deletedNote = notesService.deleteNote(id);
            if (deletedNote != null) {
                ctx.json(deletedNote.toJson());
            } else {
                ctx.status(404).result("Note not found");
            }
        });

        // Search notes by title
        app.get("/notes/search", ctx -> {
            // TODO
            ctx.status(501).result("Not implemented");
        });
    }
}
