package controllers.web;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import models.Category;
import models.Comment;
import models.Document;
import play.db.jpa.JPA;
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

    public static void list(String keyword, long categoryId, String order, Integer page) {
        Category category = Category.findById(categoryId);
    	List<Document>documents = Document.search(keyword, categoryId, order, page);
        render(documents, category, keyword, order, page);
    }

    public static void read(long id, String slug) {
        Document document = Document.findById(id);
        if (document != null) {
            render(document);
        } else {
            notFound();
        }
    }

    public static void listComments(long id, Integer page) {
        Document document = Document.findById(id);
        notFoundIfNull(document);
        List<Comment> comments = Comment.findByDocument(document.id, page);
        render(comments, document);
    }


}
