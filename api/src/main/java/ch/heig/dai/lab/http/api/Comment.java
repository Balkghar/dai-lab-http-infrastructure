package ch.heig.dai.lab.http.api;

/**
 * Comment model.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Comment(String _id, String blogId, String author, String content) {
    public Comment(){
        this(null, null, null, null);
    }
}
