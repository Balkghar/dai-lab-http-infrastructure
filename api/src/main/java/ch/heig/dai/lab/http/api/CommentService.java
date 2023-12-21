package ch.heig.dai.lab.http.api;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

/**
 * Comment CRUD service.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class CommentService {
    /**
     * The comments collection.
     */
    private final MongoCollection<Document> commentsCollection;

    /**
     * Constructor.
     */
    public CommentService() {
        MongoDatabase database = MongoDbConnection.getDatabase();
        commentsCollection = database.getCollection("comments");
    }

    /**
     * Create a new comment.
     *
     * @param comment The comment to create.
     * @return The created comment.
     */
    public Document createComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        Document doc = new Document("_id", uuid).append("blogId", comment.blogId()).append("username", comment.username()).append("content", comment.content());
        commentsCollection.insertOne(doc);
        return doc;
    }

    /**
     * Get a comment by ID.
     *
     * @param id The ID of the comment.
     * @return The comment.
     */
    public Document getCommentById(String id) {
        return commentsCollection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Get all comments.
     *
     * @return All comments.
     */
    public List<Document> getAllComments() {
        List<Document> comments = new ArrayList<>();
        for (Document blog : commentsCollection.find()) {
            comments.add(blog);
        }
        return comments;
    }

    /**
     * Get all comments for a specified blog.
     *
     * @param id The id of the blog.
     * @return A list of the comments for the blog.
     */
    public List<Document> getCommentsForBlog(String id) {
        return commentsCollection.find(eq("blogId", id)).into(new ArrayList<>());
    }

    /**
     * Update a comment
     *
     * @param id      The ID of the comment to update.
     * @param comment The comment to update.
     * @return The updated comment.
     */
    public Document updateComment(String id, Comment comment) {
        if (comment == null) {
            return null;
        }
        Document updatedComment = new Document("username", comment.username()).append("content", comment.content());
        commentsCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedComment));
        return getCommentById(id);
    }

    /**
     * Delete a comment.
     *
     * @param id The ID of the comment to delete.
     * @return The deleted blog.
     */
    public Document deleteComment(String id) {
        Document commentToDelete = getCommentById(id);
        if (commentToDelete != null) {
            commentsCollection.deleteOne(Filters.eq("_id", id));
        }
        return commentToDelete;
    }
}
