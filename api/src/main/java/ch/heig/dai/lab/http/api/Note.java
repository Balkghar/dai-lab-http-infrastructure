package ch.heig.dai.lab.http.api;

import java.time.LocalDateTime;

public class Note {
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Note() {
    }

    public Note(String updatedTitle, String updatedContent, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = updatedTitle;
        this.content = updatedContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    @Override
    public String toString() {
        return "Note{title='" + title + '\'' + ", content='" + content + '\'' + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
}
