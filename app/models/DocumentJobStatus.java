package models;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.jpa.Model;

@Entity
public class DocumentJobStatus extends BackgroundJobStatus {

    @JsonProperty
    public long resultDocumentId;
    
    public DocumentJobStatus() {
        this.status = Status.PENDING;
    }

}
