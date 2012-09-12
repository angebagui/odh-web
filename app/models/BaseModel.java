package models;

import java.util.Date;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@MappedSuperclass
public abstract class BaseModel extends Model {

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    public Date created;

    // Hack to remove persistent property from JSON output
    private transient boolean persistent;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    public Date updated;

    @JsonGetter
    @Override
    public Long getId() {
        return this.id;
    }

    @JsonIgnore
    public Boolean getPersistent() {
        return this.persistent;
    }

    @PrePersist
    protected void onCreate() {
        this.updated = this.created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    public final static int DEFAULT_PAGINATE_COUNT = 20;

    public static List<Long> getIdsOfModels(List<? extends BaseModel> models) {
        List<Long> ids = Lists.transform(models, new Function<BaseModel, Long>() {
            @Override
            public Long apply(BaseModel model) {
                return model.id;
            }
        });
        return ids;
    }

    public static int getStartFromPage(Integer page) {
        if ((page == null) || (page < 1)) {
            page = 1;
        }
        return ((page - 1) * DEFAULT_PAGINATE_COUNT);
    }

}
