package ch.heig.dai.lab.http.api.comment;

import ch.heig.dai.lab.http.api.blog.BlogService;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Comment controller.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class CommentController implements CrudHandler {
    /**
     * The comment service.
     */
    private final CommentService commentService;

    /**
     * The blog service. Used to check if a blog exists.
     */
    private final BlogService blogService;

    /**
     * Constructor.
     *
     * @param commentService The comment service to use.
     */
    public CommentController(CommentService commentService, BlogService blogService) {
        this.commentService = commentService;
        this.blogService = blogService;
    }

    /**
     * Create a comment.
     *
     * @param ctx Context of the http query.
     */
    @Override
    public void create(@NotNull Context ctx) {
        try {
            final Comment comment = ctx.bodyAsClass(Comment.class);
            if (blogService.getBlogById(comment._blogId()) == null) {
                ctx.status(404);
                ctx.result("Blog not found");
                return;
            }

            final Comment createdComment = commentService.createComment(comment);

            if (createdComment == null) {
                ctx.status(500);
                ctx.result("Comment creation failed");
                return;
            }

            ctx.status(201);
            ctx.json(createdComment);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Bad request: " + e.getMessage());
        }
    }

    /**
     * Get a single comment.
     *
     * @param ctx Context of the http query.
     * @param id  ID of the comment to delete.
     */
    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
        final Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            ctx.status(404);
            ctx.result("Comment not found");
            return;
        }
        ctx.status(200);
        ctx.json(comment);
    }

    /**
     * Get all comments.
     *
     * @param ctx Context of the http query.
     */
    @Override
    public void getAll(@NotNull Context ctx) {
        List<Comment> allComments = commentService.getAllComments();
        if (allComments == null || allComments.isEmpty()) {
            ctx.status(404);
            ctx.result("No comments found");
            return;
        }
        ctx.status(200);
        ctx.json(allComments);
    }

    /**
     * Update a comment.
     *
     * @param ctx Context of the http query.
     * @param id  ID of the comment to delete.
     */
    @Override
    public void update(@NotNull Context ctx, @NotNull String id) {
        try {
            String commentId = ctx.pathParam("id");
            Comment comment = ctx.bodyAsClass(Comment.class);

            if (blogService.getBlogById(comment._blogId()) == null) {
                ctx.status(404);
                ctx.result("Blog not found");
                return;
            }

            Comment updatedComment = commentService.updateComment(commentId, comment);
            if (updatedComment == null) {
                ctx.status(500);
                ctx.result("Comment update failed");
                return;
            }
            ctx.status(200);
            ctx.json(updatedComment);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Bad request: " + e.getMessage());
        }
    }

    /**
     * Delete a comment.
     *
     * @param ctx Context of the http query.
     * @param id  ID of the comment to delete.
     */
    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        Comment deletedComment = commentService.deleteComment(id);
        if (deletedComment == null) {
            ctx.status(404);
            ctx.result("Comment not found");
            return;
        }
        ctx.status(200);
        ctx.json(deletedComment);
    }

    /**
     * Get all comments for a specified blog.
     *
     * @param ctx Context of the http query.
     */
    public void getAllByBlogId(@NotNull Context ctx) {
        String blogId = ctx.pathParam("id");
        List<Comment> comments = commentService.getCommentsByBlogId(blogId);
        if (comments == null || comments.isEmpty()) {
            ctx.status(404);
            ctx.result("No comments found");
            return;
        }
        ctx.status(200);
        ctx.json(comments);
    }

    /**
     * Delete all comments for a specified blog.
     *
     * @param ctx Context of the http query.
     */
    public void deleteAllByBlogId(@NotNull Context ctx) {
        String blogId = ctx.pathParam("id");
        List<Comment> comments = commentService.deleteCommentsByBlogId(blogId);
        if (comments == null || comments.isEmpty()) {
            ctx.status(404);
            ctx.result("No comments found");
            return;
        }
        ctx.status(200);
        ctx.json(comments);
    }
}
