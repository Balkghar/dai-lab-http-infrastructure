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

        // Register routes
        final NoteRoutes noteRoutes = new NoteRoutes(notesService);
        noteRoutes.registerRoutes(app);
    }
}
