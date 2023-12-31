package ch.heig.dai.lab.http.api.comment;

/**
 * Comment model.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Comment(String _id, String _blogId, String author, String content, String createdAt, String updatedAt) {
}
