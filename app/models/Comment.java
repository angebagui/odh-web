package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

@Entity
public class Comment extends BaseModel {

    @ManyToOne
    public User author;

    @Required
    public String content;

    @ManyToOne
    public Document document;

}
