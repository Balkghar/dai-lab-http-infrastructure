package ch.heig.dai.lab.http.api.blog;

import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

/**
 * Blog codec for MongoDB.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class BlogCodec implements CollectibleCodec<Blog> {
    /**
     * The document codec.
     */
    private final Codec<Document> documentCodec;

    /**
     * Constructor.
     *
     * @param documentCodec The document codec.
     */
    public BlogCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    /**
     * Encode a blog.
     *
     * @param writer         The writer.
     * @param blog           The blog to encode.
     * @param encoderContext The encoder context.
     */
    @Override
    public void encode(BsonWriter writer, Blog blog, EncoderContext encoderContext) {
        Document document = new Document();
        document.put("_id", blog._id());
        document.put("title", blog.title());
        document.put("content", blog.content());
        document.put("createdAt", blog.createdAt());
        document.put("updatedAt", blog.updatedAt());
        documentCodec.encode(writer, document, encoderContext);
    }

    /**
     * Get the class of the blog.
     *
     * @return The class.
     */
    @Override
    public Class<Blog> getEncoderClass() {
        return Blog.class;
    }

    /**
     * Decode a blog.
     *
     * @param reader         The reader.
     * @param decoderContext The decoder context.
     * @return The decoded blog.
     */
    @Override
    public Blog decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        return new Blog(document.getString("_id"), document.getString("title"), document.getString("content"),
                        document.getString("createdAt"), document.getString("updatedAt"));
    }

    /**
     * Generate an ID if absent from the document.
     *
     * @param blog The blog.
     * @return The blog.
     */
    @Override
    public Blog generateIdIfAbsentFromDocument(Blog blog) {
        if (!documentHasId(blog)) {
            return new Blog(UUID.randomUUID().toString(), blog.title(), blog.content(), blog.createdAt(),
                            blog.updatedAt());
        }
        return blog;
    }

    /**
     * Check if the document has an ID.
     *
     * @param blog The blog.
     * @return True if the document has an ID, false otherwise.
     */
    @Override
    public boolean documentHasId(Blog blog) {
        return blog._id() != null;
    }

    /**
     * Get the ID of the document.
     *
     * @param blog The blog.
     * @return The ID.
     */
    @Override
    public BsonValue getDocumentId(Blog blog) {
        return new BsonString(blog._id());
    }
}