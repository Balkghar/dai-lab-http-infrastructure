package ch.heig.dai.lab.http.api;

/**
 * Comment model.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Comment(String blogId, String username, String content) {
    public Comment(){
        this(null, null, null);
    }
}
