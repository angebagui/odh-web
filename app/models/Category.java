package models;

import javax.persistence.Entity;
import javax.persistence.PreRemove;

import play.data.validation.Required;
import play.i18n.Messages;
import play.templates.JavaExtensions;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Category extends BaseModel {

    @JsonProperty
    public int documentCount;

    @Required
    @JsonProperty
    public String name;

    public Category() {
        this.documentCount = 0;
    }

    public Category(String name) {
        this.name = name;
        this.documentCount = 0;
    }

    @PreRemove
    protected void beforeDelete() {
        if (this.hasDocuments()) {
            throw new RuntimeException(Messages.get("category.delete.error.documents"));
        }
    }

    public String getSlug() {
        return JavaExtensions.slugify(this.name);
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
