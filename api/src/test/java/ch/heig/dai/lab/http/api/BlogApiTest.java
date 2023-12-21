package ch.heig.dai.lab.http.api;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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
    public void createResource_whenResourceIsValid_addsNewResource() {
        Blog createdBlog = new Blog("1", "title", "content", null, null);

        when(ctx.bodyAsClass(Blog.class)).thenReturn(createdBlog);
        when(blogService.createBlog(createdBlog)).thenReturn(createdBlog);

        blogController.create(ctx);

        verify(blogService).createBlog(createdBlog);
        verify(ctx).status(201);
        verify(ctx).json(createdBlog);
    }

    @Test
    public void createResource_whenResourceIsInvalid_returnBadRequest() {
        when(ctx.bodyAsClass(Blog.class)).thenThrow(new BadRequestResponse());

        blogController.create(ctx);

        verify(ctx).status(400);
    }

    @Test
    public void getResource_whenIdIsValid_returnsResource() {
        String id = new ObjectId().toString();
        Blog expectedBlog = new Blog(id, "title", "content", null, null);
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(expectedBlog);

        blogController.getOne(ctx, id);

        verify(blogService).getBlogById(id);
        verify(ctx).json(expectedBlog);
        verify(ctx).status(200);
    }

    @Test
    public void getResource_whenIdIsInvalid_returnsNotFound() {
        String id = "invalidId";
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(null);

        blogController.getOne(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void getAllResources_withExistingResources_returnsResources() {
        // Arrange
        List<Blog> expectedBlogs = Arrays.asList(new Blog("1", "title1", "content1", null, null), new Blog("2", "title2", "content2", null, null));
        when(blogService.getAllBlogs()).thenReturn(expectedBlogs);

        // Act
        blogController.getAll(ctx);

        // Assert
        verify(blogService).getAllBlogs();
        verify(ctx).json(expectedBlogs);
        verify(ctx).status(200);
    }

    @Test
    public void getAllResources_whenIdIsInvalid_returnsNotFound() {
        when(blogService.getAllBlogs()).thenReturn(null);

        blogController.getAll(ctx);

        verify(ctx).status(404);
    }

    @Test
    public void getAllResources_withNoResources_returnsNotFound() {
        // Arrange
        when(blogService.getAllBlogs()).thenReturn(null);

        // Act
        blogController.getAll(ctx);

        // Assert
        verify(blogService).getAllBlogs();
        verify(ctx).status(404);
        verify(ctx).result("No blogs found");
    }

    @Test
    public void updateResource_whenResourceExists_updatesResource() {
        String id = "123";
        Blog updatedBlog = new Blog(id, "updatedTitle", "updatedContent", null, null);
        when(ctx.pathParam("id")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(id, updatedBlog)).thenReturn(updatedBlog);

        blogController.update(ctx, id);

        verify(blogService).updateBlog(id, updatedBlog);
        verify(ctx).status(200); // Verify that status 200 is set
        verify(ctx).json(updatedBlog); // Verify that the response is JSON
    }

    @Test
    public void updateResource_whenIdIsInvalid_returnsNotFound() {
        String id = "invalidId";
        Blog updatedBlog = new Blog(id, "updatedTitle", "updatedContent", null, null);
        when(ctx.pathParam("id")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(id, updatedBlog)).thenReturn(null);

        blogController.update(ctx, id);

        verify(ctx).status(404);
    }

    @Test
    public void updateResource_whenResourceIsInvalid_returnsBadRequest() {
        String id = "123";
        when(ctx.pathParam("id")).thenReturn(id);
        when(ctx.bodyAsClass(Blog.class)).thenThrow(new BadRequestResponse());

        blogController.update(ctx, id);

        verify(ctx).status(400);
    }

    @Test
    public void deleteResource_whenIdIsValid_removesResource() {
        String id = "123";
        Blog deletedBlog = new Blog("1", "title", "content", null, null);
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.deleteBlog(id)).thenReturn(deletedBlog);

        blogController.delete(ctx, id);

        verify(blogService).deleteBlog(id);
        verify(ctx).status(200);
        verify(ctx).json(deletedBlog);
    }

    @Test
    public void deleteResource_whenIdIsInvalid_returnsNotFound() {
        String id = "nonExistingId";
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.deleteBlog(id)).thenReturn(null);

        blogController.delete(ctx, id);

        verify(blogService).deleteBlog(id);
        verify(ctx).status(404);
        verify(ctx).result("Blog not found");
    }
}
