package ch.heig.dai.lab.http.api.blog;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        try {
            final Blog blog = ctx.bodyAsClass(Blog.class);
            final Blog createdBlog = blogService.createBlog(blog);

            if (createdBlog == null) {
                ctx.status(500);
                ctx.result("Blog creation failed");
                return;
            }

            ctx.status(201);
            ctx.json(createdBlog);
        } catch (BadRequestResponse e) {
            ctx.status(400);
        }
    }

    /**
     * Read a blog by its ID.
     *
     * @param ctx The Javalin context.
     */
    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
        final Blog blog = blogService.getBlogById(id);
        if (blog == null) {
            ctx.status(404);
            ctx.result("Blog not found");
            return;
        }
        ctx.status(200);
        ctx.json(blog);
    }

    /**
     * Read all blogs.
     *
     * @param ctx The Javalin context.
     */
    @Override
    public void getAll(@NotNull Context ctx) {
        final List<Blog> allBlogs = blogService.getAllBlogs();
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
        try {
            final String blogId = ctx.pathParam("id");
            Blog blog = ctx.bodyAsClass(Blog.class);

            Blog updatedBlog = blogService.updateBlog(blogId, blog);
            if (updatedBlog == null) {
                ctx.status(404);
                ctx.result("Blog update failed");
                return;
            }
            ctx.status(200);
            ctx.json(updatedBlog);
        } catch (BadRequestResponse e) {
            ctx.status(400);
        }
    }

    /**
     * Delete a blog.
     *
     * @param ctx The Javalin context.
     */
    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        final Blog deletedBlog = blogService.deleteBlog(id);
        if (deletedBlog == null) {
            ctx.status(404);
            ctx.result("Blog not found");
            return;
        }
        ctx.status(200);
        ctx.json(deletedBlog);
    }

}