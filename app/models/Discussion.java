package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Transactional;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Discussion extends BaseModel {

    public int commentCount;

    @JsonProperty
    @ManyToOne
    @Required
    public Category category;

    @JsonProperty
    @Required
    public String content;

    @JsonProperty
    @Required
    public String tags;

    @JsonProperty
    @Required
    public String title;

    @JsonProperty
    @ManyToOne
    @Required
    public User user;
    
    public int voteCount;

    @JsonGetter
    public String[] getTags() {
        String[] tagList = {}; 
        if (this.tags != null) {
            tagList = tags.split(",");
            for (int i = 0; i < tagList.length; i++) {
                tagList[i] = tagList[i].trim();
            }
        }
        return tagList;
    }

    @Transactional
    public int updateCommentCountAndSave(boolean increase) {
        if (increase) {
            this.commentCount++;
        } else {
            this.commentCount--;
        }
        this.save();
        return this.commentCount;
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

}
