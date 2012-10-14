package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

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
