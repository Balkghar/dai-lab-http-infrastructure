package ch.heig.dai.lab.http.api;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.bson.Document;
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
        Blog testBlog = new Blog();
        Document createdBlog = new Document();

        when(ctx.bodyAsClass(Blog.class)).thenReturn(testBlog);
        when(blogService.createBlog(testBlog)).thenReturn(createdBlog);

        blogController.create(ctx);

        verify(blogService).createBlog(testBlog);
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
        String expectedBlog = "{\"_id\": {\"$oid\": \"" + id + "\"}, \"title\": \"title1\", \"content\": \"content1\"}";
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(Document.parse(expectedBlog));

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
        List<Document> expectedDocs = Arrays.asList(new Document("title", "title1").append("content", "content1"), new Document("title", "title2").append("content", "content2"));
        when(blogService.getAllBlogs()).thenReturn(expectedDocs);

        // Act
        blogController.getAll(ctx);

        // Assert
        verify(blogService).getAllBlogs();
        verify(ctx).json(expectedDocs);
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
        String blogId = "123";
        Blog updatedBlog = new Blog("updatedTitle", "updatedContent", null, null);
        Document updatedDocument = new Document();
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(blogId, updatedBlog)).thenReturn(updatedDocument);

        blogController.update(ctx, blogId);

        verify(blogService).updateBlog(blogId, updatedBlog);
        verify(ctx).status(200); // Verify that status 200 is set
        verify(ctx).json(updatedDocument); // Verify that the response is JSON
    }

    @Test
    public void updateResource_whenIdIsInvalid_returnsNotFound() {
        String id = "invalidId";
        Blog updatedBlog = new Blog("updatedTitle", "updatedContent", null, null);
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
        String blogId = "123";
        Document deletedBlog = new Document();
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(deletedBlog);

        blogController.delete(ctx, blogId);

        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(200);
        verify(ctx).json(deletedBlog);
    }

    @Test
    public void deleteResource_whenIdIsInvalid_returnsNotFound() {
        String blogId = "nonExistingId";
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(null);

        blogController.delete(ctx, blogId);

        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(404);
        verify(ctx).result("Blog not found");
    }
}
