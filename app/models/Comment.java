package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import net.sf.oval.constraint.MinSize;

import com.fasterxml.jackson.annotation.JsonProperty;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Transactional;

@Entity
public class Comment extends BaseModel {

    @ManyToOne
    @JsonProperty
    public User author;

    @JsonProperty
    @Required
    public String content;

    @ManyToOne
    @JsonProperty
    public Document document;

    public Comment(User author, String content, Document document) {
        this.author = author;
        this.content = content;
        this.document = document;
    }

    public static List<Comment> findByDocument(long documentId, int page) {
        List<Comment> comments = Comment.find("document.id is ? order by created asc", documentId).fetch(page, DEFAULT_PAGINATE_COUNT);
        return comments;
    }

}
