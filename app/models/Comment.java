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

    @JsonProperty
    public long repliesCount;

    @Required
    @JsonProperty
    public boolean isCensored;
    
    @Required
    @JsonProperty
    public boolean isDeleted;
    
    @ManyToOne
    @JsonProperty
    public Comment parent;
    
    public Comment(User author, String content, Document document) {
        this.author = author;
        this.content = content;
        this.document = document;
        this.isCensored = false;
        this.isDeleted = false;
    }
    
    @Transactional
    public void increaseRepliesCountAndSave() {
        this.repliesCount++;
        this.save();
    }
    
    public static List<Comment> findByDocument(long documentId, int page) {
        List<Comment> comments = new ArrayList<Comment>();
        comments = Comment.find("document.id is ? and parent is null order by created asc", documentId).fetch(page, DEFAULT_PAGINATE_COUNT);
        return comments;
    }
    
    public static List<Comment> findByParent(long parentId, int page) {
        List<Comment> replies = new ArrayList<Comment>();
        if (parentId > 0) {
            replies = Comment.find("parent.id is ? order by created asc", parentId).fetch(page, DEFAULT_PAGINATE_COUNT);
        }
        return replies;
    }

}
