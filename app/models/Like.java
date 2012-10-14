package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonProperty;

import play.data.validation.Required;

@Entity(name="like_")
public class Like extends BaseModel {

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

    private static void deleteAllForObject(String objectType, long objectId) {
        if (objectType != null) {
            Like.delete("objectType is ? and objectId is ?", objectType, objectId);
        } else {
            throw new RuntimeException("objectType parameter can't be null");
        }
    }

    public static void deleteAllForDocument(long id) {
        deleteAllForObject("document", id);
    }

}
