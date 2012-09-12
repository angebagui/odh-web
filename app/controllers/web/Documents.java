package controllers.web;

import java.util.Date;
import java.util.List;

import models.Category;
import models.Document;
import play.i18n.Messages;
import play.mvc.With;
import controllers.AppController;

@With(WebController.class)
public class Documents extends AppController {

    public static void add() {
        List<Category> categories = Category.all().fetch();
        render(categories);
    }

    public static void editDetails(long id) {
        Document document = Document.findById(id);
        notFoundIfNull(document);
        render(document);
    }

    public static void go(long id) {
        Document document = Document.findById(id);
        read(document.id, document.slug);
    }

    public static void incrementReadCount(long id) {
        checkAuthenticity();
        Document document = Document.findById(id);
        if (document != null) {
            String sessionKey = String.format("lastReadId", id);

            if (session.get(sessionKey) != null) {
                if (session.get(sessionKey) != document.id.toString()) {
                    document.incrementReadCountAndSave();
                    session.put(sessionKey, document.id);
                }
            } else {
                document.incrementReadCountAndSave();
                session.put(sessionKey, new Date().getTime());
            }

        } else {
            notFound();
        }
        ok();
    }

    public static void list(long categoryId, String categorySlug, String order, Integer page) {
        Category category = null;
        if (categoryId > 0) {
            category = Category.findById(categoryId);
            if (category == null) {
                notFound();
            }
            List<Document> documents = Document.findByCategory(category, order, page);
            render(category, documents, order, page);
        } else {
            List<Document> documents = Document.find(order, page);
            render(documents, order, page);
        }

    }

    public static void listClones(long id) {
        Document document = Document.findById(id);
        notFoundIfNull(document);
        List<Document> clones = Document.find("originalDocument is ? and isArchived is false", document).fetch();
        render(document, clones);
    }

    public static void read(long id, String slug) {
        Document document = Document.findById(id);
        if (document != null) {
            if (document.isArchived) {
                flash.error(Messages.get("document.read.error"));
                redirect("/");
            } else {
                render(document);
            }
        } else {
            notFound();
        }
    }

    public static void search(String keyword, long categoryId, String order, Integer page) {
        // List<Document> documents = Document.search(c, o, q, start);
        // render(documents);
    }

}
