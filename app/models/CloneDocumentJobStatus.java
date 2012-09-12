package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class CloneDocumentJobStatus extends Model {

    public long clonedDocumentId;
    public boolean done;

}
