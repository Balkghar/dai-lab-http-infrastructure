package ch.heig.dai.lab.http.api.blog;

import ch.heig.dai.lab.http.api.MongoDbConnection;
import ch.heig.dai.lab.http.api.comment.CommentService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.javalin.http.BadRequestResponse;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Storage service for the blog API.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogService {
    /**
     * The blog collection.
     */
    private final MongoCollection<Blog> blogCollection;

    /**
     * Constructor.
     */
    public BlogService() {
        MongoDatabase database = MongoDbConnection.getDatabase();
        blogCollection = database.getCollection("blogs", Blog.class);
    }

    /**
     * Create a new blog.
     *
     * @param blog The blog to create.
     * @return The created blog.
     */
    public Blog createBlog(Blog blog) {
        if (blog == null) {
            throw new NullPointerException("Blog must not be null");
        } else if (blog.title() == null || blog.content() == null) {
            throw new NullPointerException("Invalid blog");
        }
        String uuid = UUID.randomUUID().toString();
        String now = LocalDateTime.now().toString();
        Blog blogWithId = new Blog(uuid, blog.title(), blog.content(), now, now);
        blogCollection.insertOne(blogWithId);
        return blogWithId;
    }

    /**
     * Get a blog by ID.
     *
     * @param id The ID of the blog to get.
     * @return The blog.
     */
    public Blog getBlogById(String id) {
        if (id == null) {
            throw new NullPointerException("Id must not be null");
        }
        return blogCollection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Get all blogs.
     *
     * @return All blogs.
     */
    public List<Blog> getAllBlogs() {
        return blogCollection.find().into(new ArrayList<>());
    }

    /**
     * Update a blog.
     *
     * @param id   The ID of the blog to update.
     * @param blog The blog to update.
     * @return The updated blog.
     */
    public Blog updateBlog(String id, Blog blog) {
        if (id == null || blog == null) {
            throw new NullPointerException("Blog and id must not be null");
        } else if (blog.title() == null || blog.content() == null) {
            throw new BadRequestResponse("Invalid blog");
        }
        Document updatedBlog = new Document("title", blog.title()).append("content", blog.content());
        updatedBlog.append("updatedAt", LocalDateTime.now().toString());
        blogCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedBlog));
        return getBlogById(id);
    }

    /**
     * Delete a blog.
     *
     * @param id The ID of the blog to delete.
     * @return The deleted blog.
     */
    public Blog deleteBlog(String id) {
        if (id == null) {
            throw new NullPointerException("Id must not be null");
        }
        Blog blogToDelete = getBlogById(id);
        if (blogToDelete == null) {
            throw new NullPointerException("Blog must not be null");
        }
        // Delete all comments for the blog.
        new CommentService().deleteCommentsByBlogId(id);
        blogCollection.deleteOne(Filters.eq("_id", id));
        return blogToDelete;
    }
}
