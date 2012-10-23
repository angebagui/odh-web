package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DiscussionDocument extends BaseModel {

    @ManyToOne
    public Document document;

    @ManyToOne
    public Discussion discussion;

    @ManyToOne
    public User user;

}
