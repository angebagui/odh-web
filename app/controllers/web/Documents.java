package controllers.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import jobs.CloneDocumentJob;
import jobs.IncrementDocumentDownloadCountJob;
import models.Category;
import models.CloneDocumentJobStatus;
import models.Document;
import models.ExportLink;
import models.User;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.With;
import controllers.AppController;

@With(WebController.class)
public class Documents extends AppController {

    public static void add() {
        List<Category> categories = Category.all().fetch();
        render(categories);
    }

    public static void checkCloneDocumentJobStatus(long cloneDocumentJobStatusId) {
        CloneDocumentJobStatus cloneDocumentJobStatus = CloneDocumentJobStatus.findById(cloneDocumentJobStatusId);
        notFoundIfNull(cloneDocumentJobStatus);
        renderJSON(cloneDocumentJobStatus);
    }

    public static void createClone(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Document document = Document.findById(id);
        notFoundIfNull(document);
        if (me != document.owner) {
            CloneDocumentJobStatus cloneDocumentJobStatus = new CloneDocumentJobStatus();
            cloneDocumentJobStatus.done = false;
            cloneDocumentJobStatus.save();
            new CloneDocumentJob(cloneDocumentJobStatus.id, document.id, me.id).in(1);
            renderJSON(cloneDocumentJobStatus);
        } else {
            error();
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Document document = Document.findById(id);
        notFoundIfNull(document);
        if (document.owner.id == me.id) {
            document.isArchived = true;
            document.save();
            if (document.originalDocument != null) {
                document.originalDocument.decreaseCloneCountAndSave();
            }
            flash.success(Messages.get("document.deleted.success"));
            redirect("/");
        } else {
            read(document.id, document.slug);
        }
    }

    public static void download(long exportLinkId) {
        checkAuthenticity();
        ExportLink exportLink = ExportLink.findById(exportLinkId);
        notFoundIfNull(exportLink);
        new IncrementDocumentDownloadCountJob(exportLink.document.id).in(30);
        redirect(exportLink.link);
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

    public static void updateDetails(long id, @Required String title, @Required Category category, String source) throws IOException {
        checkAuthenticity();
        User me = Auth.getMe();
        Document document = Document.findById(id);
        notFoundIfNull(document);
        if (document.owner.id == me.id) {
            if (!validation.hasErrors()) {
                document.title = title;
                document.category = category;
                document.source = source;
                document.save();
                flash.success(Messages.get("document.updateDetails.success"));
            } else {
                renderTemplate("web/Documents/editDetails.html", document);
            }
        } else {
            flash.error("auth.unauthorizedAccess");
        }
        read(document.id, document.slug);
    }
}
