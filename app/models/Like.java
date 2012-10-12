package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.data.validation.Required;

@Entity
public class Like extends BaseModel {

    @Required
    @ManyToOne
    public User user;

    @Required
    public String objectType;
    
    @Required
    public long objectId;

}
