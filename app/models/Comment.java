
package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;

@Entity
public class Comment extends BaseModel {

    @Required
    public String content;

    @ManyToOne
    public User author;

    @ManyToOne
    public Document document;

}
