package ch.heig.dai.lab.http.api;

import ch.heig.dai.lab.http.api.blog.Blog;
import ch.heig.dai.lab.http.api.blog.BlogController;
import ch.heig.dai.lab.http.api.blog.BlogService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the blog CRUD operations.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogApiTest {
    private BlogService blogService;
    private BlogController blogController;
    private Context ctx;

    /**
     * Set up the mocks.
     */
    @BeforeEach
    public void setUp() {
        blogService = mock(BlogService.class);
        blogController = new BlogController(blogService);
        ctx = mock(Context.class);
    }

    @Test
    public void createBlog_whenBlogIsValid_addsNewBlog() {
        Blog createdBlog = new Blog("1", "title", "content", null, null);

        when(ctx.bodyAsClass(Blog.class)).thenReturn(createdBlog);
        when(blogService.createBlog(createdBlog)).thenReturn(createdBlog);

        blogController.create(ctx);

        verify(blogService).createBlog(createdBlog);
        verify(ctx).status(201);
        verify(ctx).json(createdBlog);
    }

    @Test
    public void createBlog_whenBlogIsInvalid_returnBadRequest() {
        Blog invalidBlog = new Blog(null, null, null, null, null);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(invalidBlog);
        doThrow(new BadRequestResponse()).when(blogService).createBlog(invalidBlog);

        blogController.create(ctx);

        verify(ctx).status(400);
    }

    @Test
    public void getBlog_whenIdIsValid_returnsBlog() {
        String id = new ObjectId().toString();
        Blog expectedBlog = new Blog(id, "title", "content", null, null);
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(expectedBlog);

        blogController.getOne(ctx, id);

        verify(blogService).getBlogById(id);
        verify(ctx).json(expectedBlog);
        verify(ctx).status(200);
    }

    @Test
    public void getBlog_whenIdIsInvalid_returnsNotFound() {
        String id = "invalidId";
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(null);

        blogController.getOne(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void getAllBlogs_withExistingBlogs_returnsBlogs() {
        // Arrange
        List<Blog> expectedBlogs = Arrays.asList(new Blog("1", "title1", "content1", null, null),
                                                 new Blog("2", "title2", "content2", null, null));
        when(blogService.getAllBlogs()).thenReturn(expectedBlogs);

        // Act
        blogController.getAll(ctx);

        // Assert
        verify(blogService).getAllBlogs();
        verify(ctx).json(expectedBlogs);
        verify(ctx).status(200);
    }

    @Test
    public void getAllBlogs_whenIdIsInvalid_returnsNotFound() {
        when(blogService.getAllBlogs()).thenReturn(null);

        blogController.getAll(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void getAllBlogs_withNoBlogs_returnsNotFound() {
        when(blogService.getAllBlogs()).thenReturn(null);

        blogController.getAll(ctx);

        verify(blogService).getAllBlogs();
        verify(ctx).status(404);
        verify(ctx).result("No blogs found");
    }

    @Test
    public void updateBlog_whenBlogIsValid_updatesBlog() {
        String id = "1";
        Blog updatedBlog = new Blog(id, "updatedTitle", "updatedContent", null, null);
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.getBlogById(id)).thenReturn(new Blog(id, "title", "content", null, null));
        when(blogService.updateBlog(id, updatedBlog)).thenReturn(updatedBlog);

        blogController.update(ctx, id);

        verify(blogService).updateBlog(id, updatedBlog);
        verify(ctx).status(200);
        verify(ctx).json(updatedBlog);
    }

    @Test
    public void updateBlog_whenIdIsInvalid_returnsNotFound() {
        String id = "invalidId";
        Blog updatedBlog = new Blog(id, "updatedTitle", "updatedContent", null, null);
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(id, updatedBlog)).thenReturn(null);

        blogController.update(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void updateBlog_whenBlogIsInvalid_returnsBadRequest() {
        String id = "1";
        Blog invalidBlog = new Blog(id, null, null, null, null);
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(invalidBlog);
        when(blogService.getBlogById(id)).thenReturn(new Blog(id, "title", "content", null, null));
        doThrow(new BadRequestResponse()).when(blogService).updateBlog(id, invalidBlog);

        assertThrows(BadRequestResponse.class, () -> blogController.update(ctx, id));
    }

    @Test
    public void deleteBlog_whenIdIsValid_removesBlog() {
        String id = "123";
        Blog deletedBlog = new Blog("1", "title", "content", null, null);
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(blogService.deleteBlog(id)).thenReturn(deletedBlog);

        blogController.delete(ctx, id);

        verify(blogService).deleteBlog(id);
        verify(ctx).status(200);
        verify(ctx).json(deletedBlog);
    }

    @Test
    public void deleteBlog_whenIdIsInvalid_returnsNotFound() {
        String id = "nonExistingId";
        when(ctx.pathParam("blogId")).thenReturn(id);
        when(blogService.deleteBlog(id)).thenReturn(null);

        blogController.delete(ctx, id);

        verify(blogService).deleteBlog(id);
        verify(ctx).status(404);
        verify(ctx).result("Blog not found");
    }
}
