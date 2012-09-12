package models;

import javax.persistence.MappedSuperclass;

import play.db.jpa.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
public class BackgroundJobStatus extends BaseModel {

    @JsonProperty
    public Status status;

    public static enum Status {
        PENDING, FAIL, SUCCESS
    }
}
