package ch.heig.dai.lab.http.api;

import ch.heig.dai.lab.http.api.blog.Blog;
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
    private static final Blog blog1 = new Blog("1", "title 1", "content 1", null, null);
    private static final Blog blog2 = new Blog("2", "title 2", "content 2", null, null);
    private CommentService commentService;
    private BlogService blogService;
    private CommentController commentController;
    private Context ctx;

    /**
     * Set up the mocks.
     */
    @BeforeEach
    public void setUp() {
        commentService = mock(CommentService.class);
        blogService = mock(BlogService.class);
        commentController = new CommentController(commentService, blogService);
        ctx = mock(Context.class);

        // Setup mock blogs
        when(blogService.getBlogById(blog1._id())).thenReturn(blog1);
        when(blogService.getBlogById(blog2._id())).thenReturn(blog2);
        when(blogService.getAllBlogs()).thenReturn(Arrays.asList(blog1, blog2));
        when(ctx.pathParam("blogId")).thenReturn(blog1._id());
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
        Comment invalidComment = new Comment(null, null, null, null, null,
                                             null);
        when(ctx.bodyAsClass(Comment.class)).thenReturn(invalidComment);
        doThrow(new BadRequestResponse()).when(commentService).createComment(invalidComment);

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
    public void getCommentById_whenCommentDoesNotExist_returnsNotFound() {
        String id = "invalidId";

        when(ctx.pathParam("id")).thenReturn(id);
        when(commentService.getCommentById(id)).thenReturn(null);

        commentController.getOne(ctx, id);

        verify(ctx).status(404);
    }


    @Test
    public void getAllComments_withExistingComments_returnsComments() {
        String blogId = "1";
        List<Comment> expectedComments = Arrays.asList(new Comment("1", blogId, "author1", "content1", null, null),
                                                       new Comment("2", blogId, "author2", "content2", null, null));
        when(ctx.pathParam("blogId")).thenReturn(blogId);
        when(commentService.getCommentsByBlogId(blogId)).thenReturn(expectedComments);

        commentController.getAll(ctx);

        verify(ctx).status(200);
        verify(ctx).json(expectedComments);
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
    public void updateComment_whenCommentIsValid_updatesComment() {
        Blog blog = new Blog("1", "title", "content", null, null);
        Comment updatedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(updatedComment);
        when(blogService.getBlogById(blog._id())).thenReturn(blog);
        when(ctx.pathParam("commentId")).thenReturn(updatedComment._id());
        when(commentService.updateComment(updatedComment._id(), updatedComment)).thenReturn(updatedComment);

        commentController.update(ctx, updatedComment._id());

        verify(commentService).updateComment(updatedComment._id(), updatedComment);
        verify(ctx).status(200);
        verify(ctx).json(updatedComment);
    }

    @Test
    public void updateComment_whenCommentIdIsInvalid_returnBadRequest() {
        String invalidId = "invalidId";
        Comment updatedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.bodyAsClass(Comment.class)).thenReturn(updatedComment);
        when(ctx.pathParam("commentId")).thenReturn(invalidId);
        when(commentService.updateComment(invalidId, updatedComment)).thenThrow(new BadRequestResponse());

        commentController.update(ctx, invalidId);

        verify(ctx).status(400);
    }

    @Test
    public void updateComment_whenCommentIsInvalid_returnBadRequest() {
        String blogId = "1";
        String commentId = "1";

        when(ctx.pathParam("blogId")).thenReturn(blogId);
        when(ctx.pathParam("commentId")).thenReturn(commentId);

        when(ctx.bodyAsClass(Comment.class)).thenThrow(new BadRequestResponse());

        commentController.update(ctx, commentId);

        verify(ctx).status(400);
    }

    @Test
    public void deleteComment_whenCommentIdIsValid_deletesComment() {
        Comment deletedComment = new Comment("1", "1", "title", "content", null, null);

        when(ctx.pathParam("commentId")).thenReturn(deletedComment._id());
        when(commentService.deleteComment(deletedComment._id())).thenReturn(deletedComment);

        commentController.delete(ctx, deletedComment._id());

        verify(commentService).deleteComment(deletedComment._id());
        verify(ctx).status(200);
        verify(ctx).json(deletedComment);
    }

    @Test
    public void deleteComment_whenCommentIdIsInvalid_returnsNotFound() {
        String id = "invalidId";

        when(ctx.pathParam("commentId")).thenReturn(id);
        when(commentService.deleteComment(id)).thenReturn(null);

        commentController.delete(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void deleteAllComments_withExistingComments_returnsComments() {
        String blogId = "1";
        List<Comment> expectedComments = Arrays.asList(new Comment("1", blogId, "title1", "content1", null, null),
                                                       new Comment("2", blogId, "title2", "content2", null, null));

        when(ctx.pathParam("commentId")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(expectedComments);

        commentController.deleteAll(ctx);

        verify(commentService).deleteCommentsByBlogId(blogId);
        verify(ctx).json(expectedComments);
        verify(ctx).status(200);
    }

    @Test
    public void deleteAllComments_whenIdIsInvalid_returnsNotFound() {
        String blogId = "invalidId";

        when(ctx.pathParam("blogId")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(null);

        commentController.deleteAll(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void deleteAllComments_withNoComments_returnsNotFound() {
        String blogId = "1";

        when(ctx.pathParam("blogId")).thenReturn(blogId);
        when(commentService.deleteCommentsByBlogId(blogId)).thenReturn(null);

        commentController.deleteAll(ctx);

        verify(ctx).status(404);
    }
}
