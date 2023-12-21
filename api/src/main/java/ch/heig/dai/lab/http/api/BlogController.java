package ch.heig.dai.lab.http.api;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Blog controller class.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogController implements CrudHandler {
    /**
     * The blog service to use.
     */
    private final BlogService blogService;

    /**
     * Constructor.
     *
     * @param blogService The blog service to use.
     */
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * Create a new blog.
     *
     * @param ctx The Javalin context.
     */
    @Override
    public void create(@NotNull Context ctx) {
        Blog blog = ctx.bodyAsClass(Blog.class);

        Optional<Document> createdBlog = Optional.ofNullable(blogService.createBlog(blog));

        if (createdBlog.isPresent()) {
            ctx.status(201);
            ctx.json(createdBlog.get());
        } else {
            ctx.status(500);
            ctx.result("Blog creation failed");
        }
    }

    /**
     * Read a blog by its ID.
     *
     * @param ctx The Javalin context.
     */
    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
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
    @Override
    public void getAll(@NotNull Context ctx) {
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
    @Override
    public void update(Context ctx, @NotNull String id) {
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
    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
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