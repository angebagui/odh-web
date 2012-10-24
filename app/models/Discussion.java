package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.db.jpa.GenericModel.JPAQuery;
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

    public int documentCount;
    
    public int voteCount;

    public int viewCount;

    @JsonGetter
    public String getPermalink() {
        return String.format("/discussions/%s-%s", this.id, this.slug);
    }

    @JsonGetter(value="tags")
    public String[] getTagsAsList() {
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
    
    public DiscussionDocument linkDocument(Document document, User user) {
        DiscussionDocument linkedDocument = new DiscussionDocument(this, document, user);
        if (linkedDocument.validateAndSave()) {
            return linkedDocument;
        } else {
            return null;
        }
    }

    public static List<Discussion> search(String keyword, long categoryId, String order, Integer page) {
        List<Discussion> discussions = new ArrayList<Discussion>();
        StringBuilder sb = new StringBuilder();

        sb.append("user.id != null");

        if ((keyword != null) && (keyword.length() > 0)) {
            sb.append(" and fts(:keyword) = true");
        }

        if (categoryId > 0) {
            sb.append(" and category.id is :categoryId");
        }

        if (order == null) {
            order = "recent";
        }

        if (order.equals("recent")) {
            sb.append(" order by created desc ");
        } else if (order.equals("views")) {
            sb.append(" order by viewCount desc ");
        } else if (order.equals("comments")) {
            sb.append(" order by commentCount desc ");
        } else if (order.equals("votes")) {
            sb.append(" order by voteCount desc ");
        } else if (order.equals("documents")){
            sb.append(" order by documentCount desc ");
        } else {
            throw new RuntimeException("Invalid Sort Order");
        }

        if (sb.toString() != "") {
            JPAQuery query = Discussion.find(sb.toString());

            if ((keyword != null) && (keyword.length() > 0)) {
                query.setParameter("keyword", keyword);
            }

            if (categoryId > 0) {
                query.setParameter("categoryId", categoryId);
            }

            if ((page == null) || (page < 1)) {
                page = 1;
            }

            discussions = query.fetch(page, DEFAULT_PAGINATE_COUNT);
        }
        return discussions;
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

    public boolean wasStartedByUser(User user) {
        if (user != null) {
            if (this.user != null) {
                if (this.user.id == user.id) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Discussion> findByUser(User user) {
        List<Discussion> discussions = new ArrayList<Discussion>();
        if (user != null) {
            discussions = Discussion.find("user is ? order by created desc", user).fetch(DEFAULT_PAGINATE_COUNT);
        }
        return discussions;
    }

}
