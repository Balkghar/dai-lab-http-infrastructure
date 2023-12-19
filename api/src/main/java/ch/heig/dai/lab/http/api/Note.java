package ch.heig.dai.lab.http.api;

import java.time.LocalDateTime;

public record Note(String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public Note() {
        this(null, null, null, null);
    }

    public Note(String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
