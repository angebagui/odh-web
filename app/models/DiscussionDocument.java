package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;

import play.data.validation.Required;

@Entity
public class DiscussionDocument extends BaseModel {

    @ManyToOne
    @Required
    @JsonProperty
    public Discussion discussion;

    @ManyToOne
    @Required
    @JsonProperty
    public Document document;

    @ManyToOne
    @Required
    @JsonProperty
    public User user;

    public DiscussionDocument(Discussion discussion, Document document, User user) {
        this.discussion = discussion;
        this.document = document;
        this.user = user;
    }

    public static List<DiscussionDocument> findByDiscussion(long discussionId) {
        return DiscussionDocument.find("discussion.id is ? order by created asc", discussionId).fetch();
    }

    public static List<DiscussionDocument> findByDocument(long documentId) {
        return DiscussionDocument.find("document.id is ? order by created asc", documentId).fetch();
    }

}
