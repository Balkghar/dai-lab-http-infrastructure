package ch.heig.dai.lab.http.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.bson.Document;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Routes for the blog API.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogRouter {
    /**
     * The blog service to use.
     */
    private final BlogService blogService;

    /**
     * Constructor.
     *
     * @param blogService The blog service to use.
     */
    public BlogRouter(BlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * Register the routes for the blog API.
     *
     * @param app The Javalin app to register the routes with.
     */
    public void registerRoutes(Javalin app) {
        path("api", () -> {
            app.get("/blog/{id}", this::getBlogById);
            app.get("/blog", this::getAllBlogs);
            app.post("/blog", this::createBlog);
            app.put("/blog/{id}", this::updateBlog);
            app.delete("/blog/{id}", this::deleteBlog);
        });
    }

    /**
     * Create a new blog.
     *
     * @param ctx The Javalin context.
     */
    public void createBlog(Context ctx) {
        Blog blog;
        // FIXME: remove try/catch
        try {
            blog = ctx.bodyAsClass(Blog.class);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Invalid blog format");
            return;
        }

        Document createdBlog = blogService.createBlog(blog);
        if (createdBlog == null) {
            ctx.status(500);
            ctx.result("Blog creation failed");
            return;
        }

        ctx.status(201);
        ctx.json(createdBlog);
    }

    /**
     * Read a blog by its ID.
     *
     * @param ctx The Javalin context.
     */
    public void getBlogById(Context ctx) {
        String id = ctx.pathParam("id");
        final Document blog = blogService.getBlogById(id);
        if (blog == null) {
            ctx.status(404);
            ctx.result("Blog not found");
            return;
        }
        ctx.status(200);
        ctx.json(blog.toJson());
    }

    /**
     * Read all blogs.
     *
     * @param ctx The Javalin context.
     */
    public void getAllBlogs(Context ctx) {
        List<Document> allBlogs = blogService.getAllBlogs();
        if (allBlogs == null || allBlogs.isEmpty()) {
            ctx.status(404);
            ctx.result("No blogs found");
            return;
        }
        ctx.status(200);
        ctx.json(allBlogs);
    }

    /**
     * Update a blog.
     *
     * @param ctx The Javalin context.
     */
    public void updateBlog(Context ctx) {
        String blogId = ctx.pathParam("id");
        Blog blog = ctx.bodyAsClass(Blog.class);

        Document updatedBlog = blogService.updateBlog(blogId, blog);
        if (updatedBlog == null) {
            ctx.status(500);
            ctx.result("Blog update failed");
            return;
        }
        ctx.status(200);
        ctx.json(updatedBlog);
    }

    /**
     * Delete a blog.
     *
     * @param ctx The Javalin context.
     */
    public void deleteBlog(Context ctx) {
        String id = ctx.pathParam("id");
        Document deletedBlog = blogService.deleteBlog(id);
        if (deletedBlog != null) {
            ctx.status(200);
            ctx.json(deletedBlog);
        } else {
            ctx.status(404);
            ctx.result("Blog not found");
        }
    }

}