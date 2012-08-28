
package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ExportLink extends BaseModel {

    @ManyToOne
    public Document document;

    @JsonProperty
    public String mimeType;

    @JsonProperty
    public String link;

    /**
     *
     * Constructor used during document upload to Google Drive.
     * We explicitely set the created and updated properties here because :
     * BaseModel.onCreate method does not get executed during batch insertions.
     *
     * @param document
     * @param mimeType
     * @param link
     *
     */
    public ExportLink(Document document, String mimeType, String link) {
        this.document = document;
        this.mimeType = mimeType;
        this.link = link;
        this.created = this.updated = new Date();
    }

}
