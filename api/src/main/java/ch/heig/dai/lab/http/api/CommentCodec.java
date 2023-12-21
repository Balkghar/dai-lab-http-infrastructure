package ch.heig.dai.lab.http.api;

import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

/**
 * Comment codec for MongoDB.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class CommentCodec implements CollectibleCodec<Comment> {
    /**
     * The document codec.
     */
    private final Codec<Document> documentCodec;

    /**
     * Constructor.
     *
     * @param documentCodec The document codec.
     */
    public CommentCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    /**
     * Encode a comment.
     *
     * @param writer         The writer.
     * @param comment        The comment to encode.
     * @param encoderContext The encoder context.
     */
    @Override
    public void encode(BsonWriter writer, Comment comment, EncoderContext encoderContext) {
        Document document = new Document();
        document.put("_id", comment._id());
        document.put("_blogId", comment._blogId());
        document.put("content", comment.content());
        document.put("author", comment.author());
        document.put("createdAt", comment.createdAt());
        document.put("updatedAt", comment.updatedAt());
        documentCodec.encode(writer, document, encoderContext);
    }

    /**
     * Get the class of the comment.
     *
     * @return The class.
     */
    @Override
    public Class<Comment> getEncoderClass() {
        return Comment.class;
    }

    /**
     * Decode a comment.
     *
     * @param reader         The reader.
     * @param decoderContext The decoder context.
     * @return The decoded comment.
     */
    @Override
    public Comment decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        return new Comment(document.getString("_id"), document.getString("_blogId"), document.getString("content"),
                           document.getString("author"), document.getString("createdAt"),
                           document.getString("updatedAt"));
    }

    /**
     * Generate an ID for the comment if it doesn't have one.
     *
     * @param comment The comment.
     * @return The comment with an ID.
     */
    @Override
    public Comment generateIdIfAbsentFromDocument(Comment comment) {
        if (!documentHasId(comment)) {
            return new Comment(UUID.randomUUID().toString(), comment._blogId(), comment.content(), comment.author(),
                               comment.createdAt(), comment.updatedAt());
        }
        return comment;
    }

    /**
     * Check if the comment has an ID.
     *
     * @param comment The comment.
     * @return True if the comment has an ID, false otherwise.
     */
    @Override
    public boolean documentHasId(Comment comment) {
        return comment._id() != null;
    }

    /**
     * Get the ID of the comment.
     *
     * @param comment The comment.
     * @return The ID.
     */
    @Override
    public BsonValue getDocumentId(Comment comment) {
        return new BsonString(comment._id());
    }
}