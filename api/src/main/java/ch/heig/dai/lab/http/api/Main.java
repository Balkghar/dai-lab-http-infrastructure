package ch.heig.dai.lab.http.api;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

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
        final BlogService blogService = new BlogService();
        final CommentService commentService = new CommentService();

        try (Javalin app = Javalin.create(config -> config.plugins.enableDevLogging()).start(7000)) {
            // Enable CORS for all requests
            app.before(ctx -> {
                ctx.header("Access-Control-Allow-Origin", "*");
                ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            });

            // Register 404
            app.error(404, ctx -> {
                ctx.result("Page not found");
                ctx.contentType("text/plain");
            });

        // Register routes
        app.routes(() -> {
            crud("api/blogs/{id}", new BlogController(blogService));
            crud("api/comments/{id}", new CommentController(commentService));
        });
    }
}
