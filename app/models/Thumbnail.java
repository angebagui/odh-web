package models;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Thumbnail extends Model {

    @Required
    @Lob
    public byte[] image;

    @Required
    public String mimeType;

}
