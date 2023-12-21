package ch.heig.dai.lab.http.api;

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
        // Arrange
        Blog testBlog = new Blog();
        Document createdBlog = new Document();

        when(ctx.bodyAsClass(Blog.class)).thenReturn(testBlog);
        when(blogService.createBlog(testBlog)).thenReturn(createdBlog);

        // Act
        blogController.create(ctx);

        // Assert
        verify(blogService).createBlog(testBlog);
        verify(ctx).status(201);
        verify(ctx).json(createdBlog);
    }

    @Test
    public void getResource_WhenIdIsValid_ReturnsResource() {
        // Arrange
        String id = new ObjectId().toString();
        String expectedBlog = "{\"_id\": {\"$oid\": \"" + id + "\"}, \"title\": \"title1\", \"content\": \"content1\"}";
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(Document.parse(expectedBlog));

        // Act
        blogController.getOne(ctx, id);

        // Assert
        verify(blogService).getBlogById(id);
        verify(ctx).json(expectedBlog);
        verify(ctx).status(200);
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
        // Arrange
        String blogId = "123";
        Blog updatedBlog = new Blog("updatedTitle", "updatedContent", null, null);
        Document updatedDocument = new Document();

        when(ctx.pathParam("id")).thenReturn(blogId);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(blogId, updatedBlog)).thenReturn(updatedDocument);

        // Act
        blogController.update(ctx, blogId);

        // Assert
        verify(blogService).updateBlog(blogId, updatedBlog);
        verify(ctx).status(200); // Verify that status 200 is set
        verify(ctx).json(updatedDocument); // Verify that the response is JSON
    }

    @Test
    public void deleteResource_WhenIdIsValid_RemovesResource() {
        // Arrange
        String blogId = "123";
        Document deletedBlog = new Document();
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(deletedBlog);

        // Act
        blogController.delete(ctx, blogId);

        // Assert
        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(200);
        verify(ctx).json(deletedBlog);
    }

    @Test
    public void deleteResource_WhenIdIsInvalid_returnsNotFound() {
        // Arrange
        String blogId = "nonExistingId";
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(null);

        // Act
        blogController.delete(ctx, blogId);

        // Assert
        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(404);
        verify(ctx).result("Blog not found");
    }
}
