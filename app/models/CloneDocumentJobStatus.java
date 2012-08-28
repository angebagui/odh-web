package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class CloneDocumentJobStatus extends Model {

    public boolean done;
    public long clonedDocumentId;

}
