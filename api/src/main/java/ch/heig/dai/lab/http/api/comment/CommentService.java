package ch.heig.dai.lab.http.api.comment;

import ch.heig.dai.lab.http.api.MongoDbConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.javalin.http.BadRequestResponse;
import org.bson.Document;

import java.time.LocalDateTime;
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
        if (comment == null || comment._blogId() == null || comment.author() == null || comment.content() == null) {
            throw new BadRequestResponse();
        }
        String uuid = UUID.randomUUID().toString();
        String now = LocalDateTime.now().toString();

        Comment commentWithId = new Comment(uuid, comment._blogId(), comment.author(), comment.content(), now, now);
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
        if (id == null) {
            throw new NullPointerException("Id must not be null");
        }
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
    public List<Comment> getCommentsByBlogId(String id) {
        if (id == null) {
            throw new NullPointerException("Id must not be null");
        }
        return commentsCollection.find(eq("_blogId", id)).into(new ArrayList<>());
    }

    /**
     * Update a comment
     *
     * @param id      The ID of the comment to update.
     * @param comment The comment to update.
     * @return The updated comment.
     */
    public Comment updateComment(String id, Comment comment) {
        if (id == null || comment == null) {
            throw new NullPointerException("Comment and id must not be null");
        }
        Document updatedComment = new Document("author", comment.author()).append("content", comment.content());
        // Set the updatedAt field to the current time.
        updatedComment.append("updatedAt", LocalDateTime.now().toString());
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
        if (id == null) {
            throw new NullPointerException("Id must not be null");
        }
        Comment commentToDelete = getCommentById(id);
        if (commentToDelete == null) {
            throw new NullPointerException("Comment must not be null");
        }
        commentsCollection.deleteOne(Filters.eq("_id", id));
        return commentToDelete;
    }

    /**
     * Delete all comments for a specified blog.
     *
     * @param blogId The ID of the blog to delete.
     * @return The deleted comments.
     */
    public List<Comment> deleteCommentsByBlogId(String blogId) {
        if (blogId == null) {
            throw new NullPointerException("blogId must not be null");
        }
        List<Comment> commentsToDelete = getCommentsByBlogId(blogId);
        if (commentsToDelete == null) {
            throw new NullPointerException("Comment must not be null");
        }
        commentsCollection.deleteMany(Filters.eq("_blogId", blogId));
        return commentsToDelete;
    }
}
