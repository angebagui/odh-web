
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.type.LobType;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Thumbnail extends Model {

    @Required
    @Lob
    public byte[] image;

    @Required
    public String mimeType;

}
