package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.data.validation.Required;

@Entity
public class Vote extends BaseModel {

    @Required
    @ManyToOne
    @JsonProperty
    public User user;

    @Required
    @JsonProperty
    public String objectType;
    
    @Required
    @JsonProperty
    public long objectId;

    @JsonGetter
    public int getObjectVoteCount() {
        int count = -1;
        if ("document".equals(this.objectType)) {
            Document document = Document.findById(this.objectId);
            if (document != null) {
                count = document.voteCount;
            }
        } else if ("comment".equals(this.objectType)) {
            Comment comment = Comment.findById(this.objectId);
            if (comment != null) {
                count = comment.voteCount;
            }
        } else if ("discussion".equals(this.objectType)) {
            Discussion discussion = Discussion.findById(this.objectId);
            if (discussion != null) {
                count = discussion.voteCount;
            }
        }
        return count;
    }

    public int updateCountForObject(boolean increase) {
        int count = -1;
        if ("document".equals(this.objectType)) {
            Document document = Document.findById(this.objectId);
            if (document != null) {
                count = document.updateVoteCountAndSave(increase);
            }
        } else if ("comment".equals(this.objectType)) {
            Comment comment = Comment.findById(this.objectId);
            if (comment != null) {
                count = comment.updateVoteCountAndSave(increase);
            }
        } else if ("discussion".equals(this.objectType)) {
            Discussion discussion = Discussion.findById(this.objectId);
            if (discussion != null) {
                count = discussion.updateVoteCountAndSave(increase);
            }
        }
        return count;
    }

    public static void deleteAllForComment(long id) {
        deleteAllForObject("comment", id);
    }

    public static void deleteAllForDiscussion(long id) {
        deleteAllForObject("discussion", id);
    }

    public static void deleteAllForDocument(long id) {
        deleteAllForObject("document", id);
    }

    private static void deleteAllForObject(String objectType, long objectId) {
        if (objectType != null) {
            Vote.delete("objectType is ? and objectId is ?", objectType, objectId);
        } else {
            throw new RuntimeException("objectType parameter can't be null");
        }
    }

}
