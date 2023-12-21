package ch.heig.dai.lab.http.api;

import java.time.LocalDateTime;

/**
 * Blog model.
 * FIXME: add author field
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Blog(String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public Blog() {
        this(null, null, null, null);
    }

    public Blog(String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}