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
    private BlogRouter blogRouter;
    private Context ctx;

    /**
     * Set up the mocks.
     */
    @BeforeEach
    public void setUp() {
        blogService = mock(BlogService.class);
        blogRouter = new BlogRouter(blogService);
        ctx = mock(Context.class);
    }

    @Test
    public void createBlog() {
        // Arrange
        Blog testBlog = new Blog();
        Document createdBlog = new Document();

        when(ctx.bodyAsClass(Blog.class)).thenReturn(testBlog);
        when(blogService.createBlog(testBlog)).thenReturn(createdBlog);

        // Act
        blogRouter.createBlog(ctx);

        // Assert
        verify(blogService).createBlog(testBlog);
        verify(ctx).status(201);
        verify(ctx).json(createdBlog);
    }

    @Test
    public void getBlog() {
        // Arrange
        String id = new ObjectId().toString();
        String expectedBlog = "{\"_id\": {\"$oid\": \"" + id + "\"}, \"title\": \"title1\", \"content\": \"content1\"}";
        when(ctx.pathParam("id")).thenReturn(id);
        when(blogService.getBlogById(id)).thenReturn(Document.parse(expectedBlog));

        // Act
        blogRouter.getBlogById(ctx);

        // Assert
        verify(blogService).getBlogById(id);
        verify(ctx).json(expectedBlog);
        verify(ctx).status(200);
    }

    // FIXME: extend to test when no blogs are present
    @Test
    public void getAllBlogs() {
        // Arrange
        List<Document> expectedDocs = Arrays.asList(new Document("title", "title1").append("content", "content1"), new Document("title", "title2").append("content", "content2"));
        when(blogService.getAllBlogs()).thenReturn(expectedDocs);

        // Act
        blogRouter.getAllBlogs(ctx);

        // Assert
        verify(blogService).getAllBlogs();
        verify(ctx).json(expectedDocs);
        verify(ctx).status(200);
    }

    @Test
    public void updateBlog() {
        // Arrange
        String blogId = "123";
        Blog updatedBlog = new Blog("updatedTitle", "updatedContent", null, null);
        Document updatedDocument = new Document();

        when(ctx.pathParam("id")).thenReturn(blogId);
        when(ctx.bodyAsClass(Blog.class)).thenReturn(updatedBlog);
        when(blogService.updateBlog(blogId, updatedBlog)).thenReturn(updatedDocument);

        // Act
        blogRouter.updateBlog(ctx);

        // Assert
        verify(blogService).updateBlog(blogId, updatedBlog);
        verify(ctx).status(200); // Verify that status 200 is set
        verify(ctx).json(updatedDocument); // Verify that the response is JSON
    }

    @Test
    public void deleteBlog_SuccessfulDeletion() {
        // Arrange
        String blogId = "123";
        Document deletedBlog = new Document();
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(deletedBlog);

        // Act
        blogRouter.deleteBlog(ctx);

        // Assert
        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(200);
        verify(ctx).json(deletedBlog);
    }

    @Test
    public void deleteBlog_NotFound() {
        // Arrange
        String blogId = "nonExistingId";
        when(ctx.pathParam("id")).thenReturn(blogId);
        when(blogService.deleteBlog(blogId)).thenReturn(null);

        // Act
        blogRouter.deleteBlog(ctx);

        // Assert
        verify(blogService).deleteBlog(blogId);
        verify(ctx).status(404);
        verify(ctx).result("Blog not found");
    }
}
