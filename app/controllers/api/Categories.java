package controllers.api;

import controllers.AppController;
import java.util.List;
import play.data.validation.Required;
import models.Category;
import models.Document;
import play.mvc.With;

@With(ApiController.class)
public class Categories extends AppController {

    public static void create(@Required Category category) {

    }

    public static void list() {
        List<Category> categories = Category.all().fetch();
        renderJSON(categories);
    }

    public static void listDocuments(long id, int page) {
        Category category = Category.findById(id);
        if (category != null) {
            List<Document> documents = Document.findByCategory(category, null, page);
            renderJSON(documents);
        } else {
            notFound();
        }
    }

}
