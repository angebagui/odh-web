package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.binding.NoBinding;
import play.data.validation.Required;
import play.i18n.Messages;
import play.templates.JavaExtensions;

import javax.persistence.Entity;
import javax.persistence.PreRemove;

@Entity
public class Category extends BaseModel {

    @Required
    @JsonProperty
    public String name;

    @JsonProperty
    public int documentCount;

    public Category() {
        this.documentCount = 0;
    }

    public Category(String name) {
        this.name = name;
        this.documentCount = 0;
    }

    public String getSlug() {
        return JavaExtensions.slugify(this.name);
    }

    @PreRemove
    protected void beforeDelete() {
        if (this.hasDocuments()) {
            throw new RuntimeException(Messages.get("category.delete.error.documents"));
        }
    }

    public boolean hasDocuments() {
        Document check = Document.find("category is ?", this).first();
        return (check != null);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
