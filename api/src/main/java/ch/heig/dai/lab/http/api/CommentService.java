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
    private final MongoCollection<Comment> commentsCollection;

    /**
     * Constructor.
     */
    public CommentService() {
        MongoDatabase database = MongoDbConnection.getDatabase();
        commentsCollection = database.getCollection("comments", Comment.class);
    }

    /**
     * Create a new comment.
     *
     * @param comment The comment to create.
     * @return The created comment.
     */
    public Comment createComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        Comment commentWithId = new Comment(uuid, comment.blogId(), comment.author(), comment.content());
        commentsCollection.insertOne(commentWithId);
        return commentWithId;
    }

    /**
     * Get a comment by ID.
     *
     * @param id The ID of the comment.
     * @return The comment.
     */
    public Comment getCommentById(String id) {
        return commentsCollection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Get all comments.
     *
     * @return All comments.
     */
    public List<Comment> getAllComments() {
        return commentsCollection.find().into(new ArrayList<>());
    }

    /**
     * Get all comments for a specified blog.
     *
     * @param id The id of the blog.
     * @return A list of the comments for the blog.
     */
    public List<Comment> getCommentsForBlog(String id) {
        return commentsCollection.find(eq("blogId", id)).into(new ArrayList<>());
    }

    /**
     * Update a comment
     *
     * @param id      The ID of the comment to update.
     * @param comment The comment to update.
     * @return The updated comment.
     */
    public Comment updateComment(String id, Comment comment) {
        if (comment == null) {
            return null;
        }
        Document updatedComment = new Document("author", comment.author()).append("content", comment.content());
        commentsCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedComment));
        return getCommentById(id);
    }

    /**
     * Delete a comment.
     *
     * @param id The ID of the comment to delete.
     * @return The deleted comment.
     */
    public Comment deleteComment(String id) {
        Comment commentToDelete = getCommentById(id);
        if (commentToDelete != null) {
            commentsCollection.deleteOne(Filters.eq("_id", id));
        }
        return commentToDelete;
    }
}
