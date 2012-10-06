package models;

import javax.persistence.Entity;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.jpa.Model;

@Entity
public class DocumentJobStatus extends BackgroundJobStatus {

    @JsonProperty
    @Lob
    public String result;
    
    public DocumentJobStatus() {
        this.status = Status.PENDING;
    }

}
