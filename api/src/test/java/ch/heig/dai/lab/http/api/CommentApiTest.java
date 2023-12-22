package ch.heig.dai.lab.http.api;

import ch.heig.dai.lab.http.api.blog.BlogService;
import ch.heig.dai.lab.http.api.comment.Comment;
import ch.heig.dai.lab.http.api.comment.CommentController;
import ch.heig.dai.lab.http.api.comment.CommentService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the comment CRUD operations.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class CommentApiTest {
    private CommentService commentService;
    private CommentController commentController;
    private Context ctx;

    /**
     * Set up the mocks.
     */
    @BeforeEach
    public void setUp() {
        commentService = mock(CommentService.class);
        commentController = new CommentController(commentService, mock(BlogService.class));
        ctx = mock(Context.class);
    }

    @Test
    public void createComment_whenCommentIsValid_addsNewComment() {
        Comment createdComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(createdComment);
        when(commentService.createComment(createdComment)).thenReturn(createdComment);

        commentController.create(ctx);

        verify(commentService).createComment(createdComment);
        verify(ctx).status(201);
        verify(ctx).json(createdComment);
    }

    @Test
    public void createComment_whenBlogIdIsInvalid_returnNotFound() {
        Comment createdComment = new Comment("1", "invalidId", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(createdComment);
        when(commentService.createComment(createdComment)).thenReturn(null);

        commentController.create(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void createComment_whenCommentIsInvalid_returnBadRequest() {
        when(ctx.bodyAsClass(Comment.class)).thenThrow(new BadRequestResponse());

        commentController.create(ctx);

        verify(ctx).status(400);
    }

    @Test
    public void getCommentById_whenCommentExists_returnsComment() {
        Comment comment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.pathParam("id")).thenReturn(comment._id());
        when(commentService.getCommentById(comment._id())).thenReturn(comment);

        commentController.getOne(ctx, comment._id());

        verify(ctx).json(comment);
    }

    @Test
    public void getCommentsByBlogId_whenCommentsExist_returnsComments() {
        Comment comment1 = new Comment("1", "1", "title", "content", null, null);
        Comment comment2 = new Comment("2", "1", "title", "content", null, null);
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(ctx.pathParam("id")).thenReturn(comment1._blogId());
        when(commentService.getCommentsByBlogId(comment1._blogId())).thenReturn(comments);

        commentController.getAllByBlogId(ctx);

        verify(ctx).json(comments);
    }

    @Test
    public void getCommentById_whenCommentDoesNotExist_returnsNotFound() {
        String id = "invalidId";

        when(ctx.pathParam("id")).thenReturn(id);
        when(commentService.getCommentById(id)).thenReturn(null);

        commentController.getOne(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void getCommentsByBlogId_whenBlogDoesNotExist_returnsComments() {
        String id = "invalidId";

        when(ctx.pathParam("id")).thenReturn(id);
        when(commentService.getCommentsByBlogId(id)).thenReturn(null);

        commentController.getAllByBlogId(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void updateComment_whenCommentIsValid_updatesComment() {
        Comment updatedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(updatedComment);
        when(ctx.pathParam("id")).thenReturn(updatedComment._id());
        when(commentService.updateComment(updatedComment._id(), updatedComment)).thenReturn(updatedComment);

        commentController.update(ctx, updatedComment._id());

        verify(commentService).updateComment(updatedComment._id(), updatedComment);
        verify(ctx).status(200);
        verify(ctx).json(updatedComment);
    }

    @Test
    public void updateComment_whenCommentIdIsInvalid_returnBadRequest() {
        Comment updatedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(updatedComment);
        when(ctx.pathParam("id")).thenReturn("invalidId");
        when(commentService.updateComment(updatedComment._id(), updatedComment)).thenReturn(null);

        commentController.update(ctx, updatedComment._id());

        verify(ctx).status(404);
    }

    @Test
    public void updateComment_whenCommentIsInvalid_returnBadRequest() {
        when(ctx.bodyAsClass(Comment.class)).thenThrow(new BadRequestResponse());

        commentController.update(ctx, "1");

        verify(ctx).status(400);
    }

    @Test
    public void deleteComment_whenCommentIdIsValid_deletesComment() {
        Comment deletedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.pathParam("id")).thenReturn(deletedComment._id());
        when(commentService.deleteComment(deletedComment._id())).thenReturn(deletedComment);

        commentController.delete(ctx, deletedComment._id());

        verify(commentService).deleteComment(deletedComment._id());
        verify(ctx).status(200);
        verify(ctx).json(deletedComment);
    }

    @Test
    public void deleteComment_whenCommentIdIsInvalid_returnsNotFound() {
        String id = "invalidId";

        when(ctx.pathParam("id")).thenReturn(id);
        when(commentService.deleteComment(id)).thenReturn(null);

        commentController.delete(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void getAllComments_withExistingComments_returnsComments() {
        List<Comment> expectedComments = Arrays.asList(new Comment("1", "1", "title1", "content1", null, null),
                                                       new Comment("2", "1", "title2", "content2", null, null));

        when(commentService.getAllComments()).thenReturn(expectedComments);

        commentController.getAll(ctx);

        verify(commentService).getAllComments();
        verify(ctx).json(expectedComments);
        verify(ctx).status(200);
    }

    @Test
    public void getAllComments_whenIdIsInvalid_returnsNotFound() {
        when(commentService.getAllComments()).thenReturn(null);

        commentController.getAll(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void getAllComments_withNoComments_returnsNotFound() {
        when(commentService.getAllComments()).thenReturn(null);

        commentController.getAll(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void deleteAllComments_withExistingComments_returnsComments() {
        String blogId = "1";
        List<Comment> expectedComments = Arrays.asList(new Comment("1", blogId, "title1", "content1", null, null),
                                                       new Comment("2", blogId, "title2", "content2", null, null));

        when(ctx.pathParam("id")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(expectedComments);

        commentController.deleteAllByBlogId(ctx);

        verify(commentService).deleteCommentsByBlogId(blogId);
        verify(ctx).json(expectedComments);
        verify(ctx).status(200);
    }

    @Test
    public void deleteAllComments_whenIdIsInvalid_returnsNotFound() {
        String blogId = "invalidId";

        when(ctx.pathParam("id")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(null);

        commentController.deleteAllByBlogId(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void deleteAllComments_withNoComments_returnsNotFound() {
        String blogId = "1";

        when(ctx.pathParam("id")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(null);

        commentController.deleteAllByBlogId(ctx);

        verify(ctx).status(404);
    }
}
