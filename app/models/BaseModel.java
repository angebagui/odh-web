package models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.collect.Lists;
import com.google.common.base.Function;
import java.util.List;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import play.db.jpa.Model;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@MappedSuperclass
public abstract class BaseModel extends Model {
    
    public final static int DEFAULT_PAGINATE_COUNT = 20;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    public Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    public Date updated;

    // Hack to remove persistent property from JSON output
    private transient boolean persistent;

    @PrePersist
    protected void onCreate() {
        this.updated = this.created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    @JsonIgnore
    public Boolean getPersistent() {
        return this.persistent;
    }
    
    public static List<Long> getIdsOfModels(List<? extends BaseModel> models) {
        List<Long> ids = Lists.transform(models, new Function<BaseModel, Long>() {
            public Long apply(BaseModel model) {
                return model.id;
            }
        });
        return ids;
    }

    public static int getStartFromPage(Integer page) {
        if (page == null || page < 1) {
            page = 1;
        }
        return ((page - 1) * DEFAULT_PAGINATE_COUNT);
    }

    @JsonGetter
    @Override
    public Long getId() {
        return this.id;
    }
    
}
