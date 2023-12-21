package ch.heig.dai.lab.http.api;

/**
 * Blog model.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Blog(String _id, String title, String content, String createdAt, String updatedAt) {
}
