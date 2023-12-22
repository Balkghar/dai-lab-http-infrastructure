package ch.heig.dai.lab.http.api;

import ch.heig.dai.lab.http.api.blog.BlogController;
import ch.heig.dai.lab.http.api.blog.BlogService;
import ch.heig.dai.lab.http.api.comment.CommentController;
import ch.heig.dai.lab.http.api.comment.CommentService;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.crud;

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

        // Create the Javalin app
        Javalin app = Javalin.create(config -> config.plugins.enableDevLogging()).start(7000);

        // Enable CORS for all requests
        app.before(ctx -> ctx.header("Access-Control-Allow-Origin", "*")
                             .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"));


        // Register error handler
        app.error(404, ctx -> ctx.result("Page not found").contentType("text/plain"));
        // Register exception handler
        app.exception(Exception.class, (e, ctx) -> ctx.status(500).result("Internal server error"));

        // Register routes
        app.routes(() -> {
            crud("api/blogs/{id}", new BlogController(blogService));
            crud("api/comments/{id}", new CommentController(commentService, blogService));
            app.get("api/blogs/{id}/comments", new CommentController(commentService, blogService)::getAllByBlogId);
        });
    }
}
