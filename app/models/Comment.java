package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;

import net.sf.oval.constraint.MinSize;

import com.fasterxml.jackson.annotation.JsonGetter;
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

    @JsonProperty
    public long objectId;

    @JsonProperty
    public String objectType;
    
    @JsonProperty
    public int voteCount;

    public Comment(User author, String content, long objectId) {
        this.author = author;
        this.content = content;
        this.objectId = objectId;
    }

    @JsonGetter
    public int getObjectCommentCount() {
        int count = -1;
        if ("document".equals(this.objectType)) {
            Document document = Document.findById(this.objectId);
            if (document != null) {
                count = document.commentCount;
            }
        } else if ("discussion".equals(this.objectType)) {
            Discussion discussion = Discussion.findById(this.objectId);
            if (discussion != null) {
                count = discussion.commentCount;
            }
        }
        return count;
    }

    public int updateCountForObject(boolean increase) {
        int count = -1;
        if ("document".equals(this.objectType)) {
            Document document = Document.findById(this.objectId);
            if (document != null) {
                count = document.updateCommentCountAndSave(increase);
            }
        } else if ("discussion".equals(this.objectType)) {
            Discussion discussion = Discussion.findById(this.objectId);
            if (discussion != null) {
                count = discussion.updateCommentCountAndSave(increase);
            }
        }
        return count;
    }

    @Transactional
    public int updateVoteCountAndSave(boolean increment) {
        if (increment) {
            this.voteCount++;
        } else {
            this.voteCount--;
        }
        this.save();
        return this.voteCount;
    }

    public static List<Comment> findByObject(String objectType, long objectId, int page) {
        if (objectType != null) {
            return Comment.find("objectType is ? and objectId is ? order by created asc", objectType, objectId).fetch(page, DEFAULT_PAGINATE_COUNT);
        } else {
            throw new RuntimeException("Please specify objectType parameter");
        }
    }

    public static int deleteAllForObject(String objectType, long objectId) {
        if (objectType != null) {
            return Comment.delete("objectType is ? and objectId is ?", objectType, objectId);
        } else {
            throw new RuntimeException("Please specify objectType parameter");
        }
    }

}