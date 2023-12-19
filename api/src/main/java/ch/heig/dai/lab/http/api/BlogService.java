package ch.heig.dai.lab.http.api;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage service for the blog API.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogService {
    /**
     * The blogs collection.
     */
    private final MongoCollection<Document> blogsCollection;

    /**
     * Constructor.
     */
    public BlogService() {
        MongoDatabase database = MongoDbConnection.getDatabase();
        blogsCollection = database.getCollection("blogs");
    }

    /**
     * Create a new blog.
     *
     * @param blog The blog to create.
     * @return The created blog.
     */
    public Document createBlog(Blog blog) {
        if (blog == null) {
            return null;
        }
        Document doc = new Document("title", blog.title()).append("content", blog.content());
        blogsCollection.insertOne(doc);
        return doc;
    }

    /**
     * Get a blog by ID.
     *
     * @param id The ID of the blog to get.
     * @return The blog.
     */
    public Document getBlogById(String id) {
        return blogsCollection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    /**
     * Get all blogs.
     *
     * @return All blogs.
     */
    public List<Document> getAllBlogs() {
        List<Document> blogs = new ArrayList<>();
        for (Document blog : blogsCollection.find()) {
            blogs.add(blog);
        }
        return blogs;
    }

    /**
     * Update a blog.
     * FIXME: remove try/catch
     *
     * @param id   The ID of the blog to update.
     * @param blog The blog to update.
     * @return The updated blog.
     */
    public Document updateBlog(String id, Blog blog) {
        if (blog == null) {
            return null;
        }
        Document updatedBlog = new Document("title", blog.title()).append("content", blog.content());
        try {
            blogsCollection.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", updatedBlog));
        } catch (Exception e) {
            return null;
        }
        return getBlogById(id);
    }

    /**
     * Delete a blog.
     *
     * @param id The ID of the blog to delete.
     * @return The deleted blog.
     */
    public Document deleteBlog(String id) {
        Document blogToDelete = getBlogById(id);
        if (blogToDelete != null) {
            blogsCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
        }
        return blogToDelete;
    }
}
