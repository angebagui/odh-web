package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.templates.JavaExtensions;

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
    public String slug;

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
    
    //
    public int documentCount;
    
    public int voteCount;
    
    // evolutions
    public int viewCount;

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
    public int increaseViewCountAndSave() {
        this.viewCount++;
        this.save();
        return this.viewCount;
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
    
    @PrePersist
    @PreUpdate
    protected void preSave() {
        this.slug = JavaExtensions.slugify(this.title);
    }

    public int updateDocumentCountAndSave(boolean increase) {
        if (increase) {
            this.documentCount++;
        } else {
            this.documentCount--;
        }
        this.save();
        return this.documentCount;
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
