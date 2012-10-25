package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PreRemove;

import play.data.validation.Required;
import play.db.jpa.GenericModel.JPAQuery;
import play.i18n.Messages;
import play.templates.JavaExtensions;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Category extends BaseModel {

    @Required
    @JsonProperty
    public String name;
    
    @Required
    @JsonProperty
    public String objectType;

    public Category(String name, String objectType) {
        this.name = name;
        this.objectType = objectType;
    }

    @PreRemove
    public void beforeDelete() {
        if (this.hasObjects(this.objectType)) {
            throw new RuntimeException(Messages.get("category.delete.error.documents"));
        }
    }

    public String getSlug() {
        return JavaExtensions.slugify(this.name);
    }

    public boolean hasObjects(String objectType) {
        boolean hasObjects = false;
        if ("document".equals(this.objectType)) {
            Document check = Document.find("category is ?", this).first();
            hasObjects = (check != null);
        } else if ("discussion".equals(this.objectType)) {
            Discussion check = Discussion.find("category is ?", this).first();
            hasObjects = (check != null);
        }
        return hasObjects;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.objectType + ")";
    }

    public static List<Category> findForDocument() {
        return findForObjectType("document");
    }
    
    public static List<Category> findForDiscussion() {
        return findForObjectType("discussion");
    }
    
    private static List<Category> findForObjectType(String objectType) {
        if (objectType != null && ("document".equals(objectType) || "discussion".equals(objectType))) {
            return Category.find("objectType is ? order by name", objectType).fetch();
        } else {
            throw new RuntimeException("Invalid object type.");
        }
    }

}
