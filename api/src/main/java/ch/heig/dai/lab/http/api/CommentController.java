package ch.heig.dai.lab.http.api;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
     * Constructor.
     *
     * @param commentService The comment service to use.
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Create a comment.
     *
     * @param ctx Context of the http query.
     */
    @Override
    public void create(@NotNull Context ctx) {
        final Comment comment = ctx.bodyAsClass(Comment.class);
        final Optional<Comment> createdComment = Optional.ofNullable(commentService.createComment(comment));

        if (createdComment.isPresent()) {
            ctx.status(201);
            ctx.json(createdComment.get());
        } else {
            ctx.status(500);
            ctx.result("Comment creation failed");
        }
    }

    /**
     * Get a single comment.
     *
     * @param ctx Context of the http query.
     * @param id ID of the comment to delete.
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
     * @param id ID of the comment to delete.
     */
    @Override
    public void update(@NotNull Context ctx, @NotNull String id) {
        String commentId = ctx.pathParam("id");
        Comment comment = ctx.bodyAsClass(Comment.class);

        Comment updatedComment = commentService.updateComment(commentId, comment);
        if (updatedComment == null) {
            ctx.status(500);
            ctx.result("Comment update failed");
            return;
        }
        ctx.status(200);
        ctx.json(updatedComment);
    }

    /**
     * Delete a comment.
     *
     * @param ctx Context of the http query.
     * @param id ID of the comment to delete.
     */
    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        Comment deletedComment = commentService.deleteComment(id);
        if (deletedComment != null) {
            ctx.status(200);
            ctx.json(deletedComment);
        } else {
            ctx.status(404);
            ctx.result("Comment not found");
        }
    }
}
