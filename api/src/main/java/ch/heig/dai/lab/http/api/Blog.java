package ch.heig.dai.lab.http.api;

import java.time.LocalDateTime;

/**
 * Blog model.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Blog(String _id, String title, String content, String createdAt, String updatedAt) {
    public Blog() {
        this(null, null, null, null, null);
    }
}
