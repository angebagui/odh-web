package controllers.web;

import java.util.List;

import models.Category;
import models.Discussion;
import models.Document;

import play.mvc.With;

import controllers.AppController;

@With(WebController.class)
public class Discussions extends AppController {

    public static void add(long taggedDocumentId) {
        if (taggedDocumentId > 0) {
            Document taggedDocument = Document.findById(taggedDocumentId);
            notFoundIfNull(taggedDocument);
            renderArgs.put("taggedDocument", taggedDocument);
        }
        List<Category> categories = Category.findForDiscussions();
        render(categories);
    }
    
    public static void go(long id) {
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        view(discussion.id, discussion.slug);
    }
    
    public static void list() {
        render();
    }
    
    public static void view(long id, String slug) {
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        render(discussion);
    }
}
