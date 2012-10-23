package controllers.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import jobs.CopyDocumentJob;
import jobs.IncrementDocumentDownloadCountJob;
import models.Category;
import models.Comment;
import models.Document;
import models.DocumentJobStatus;
import models.ExportLink;
import models.Vote;
import models.User;
import models.enums.Mime;
import play.Logger;
import play.data.Upload;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.With;
import controllers.AppController;
import controllers.web.Auth;

@With(ApiController.class)
public class Documents extends AppController {

    public static void create(@Required Document document, @Required Upload file) {
        User me = Auth.getMe();
        if (!validation.hasErrors()) {
            document.setMime(Mime.parseName(file.getContentType()));
            try {
                document.owner = me;
                if (validation.valid(document).ok) {
                    document.uploadToGoogleDriveAndSave(file);
                }
                renderJSON(document);
            } catch (IOException e) {
                Logger.error("Could not insert file on Google Drive :" + "\n\t File Name : %s" + "\n\t File Size : %s" + "\n\t File Owner ID : %s" + "\n\t Exception Message : %s", file.getFileName(), file.getSize(), me.id, e.getMessage());
                response.status = 500;
                renderJSON(e.getMessage());
            }

        }
    }

    public static void createCopy(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Document document = Document.findById(id);
        notFoundIfNull(document);
        if (me != document.owner) {
            DocumentJobStatus documentJobStatus = new DocumentJobStatus();
            documentJobStatus.save();
            new CopyDocumentJob(documentJobStatus.id, document.id, me.id).in(1);
            renderJSON(documentJobStatus);
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
            Vote.deleteAllForDocument(document.id);
            document.delete();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

    public static void download(long exportLinkId) {
        checkAuthenticity();
        ExportLink exportLink = ExportLink.findById(exportLinkId);
        notFoundIfNull(exportLink);
        new IncrementDocumentDownloadCountJob(exportLink.document.id).in(30);
        redirect(exportLink.link);
    }

    public static void list(String keyword, long categoryId, String order, Integer page) {
        List<Document> documents = Document.search(keyword, categoryId, order, page);
        renderJSON(documents);
    }

    public static void markAsViewed(long id) {
        checkAuthenticity();
        Document document = Document.findById(id);
        if (document != null) {
            String sessionKey = String.format("lastViewedId", id);

            if (session.get(sessionKey) != null) {
                if (session.get(sessionKey) != document.id.toString()) {
                    document.incrementViewCountAndSave();
                    session.put(sessionKey, document.id);
                }
            } else {
                document.incrementViewCountAndSave();
                session.put(sessionKey, new Date().getTime());
            }
        } else {
            notFound();
        }
        ok();
    }
    
    public static void read(long id) {
        Document document = Document.findById(id);
        notFoundIfNull(document);
        renderJSON(document);
    }

    public static void readThumbnail(long id) {
        Document document = Document.findById(id);
        if (document != null) {
            if (document.thumbnail != null) {
                ByteArrayInputStream thumbnailStream = new ByteArrayInputStream(document.thumbnail.image);
                if (thumbnailStream != null) {
                    response.cacheFor("120mn");
                    renderBinary(thumbnailStream);
                }
            }
            File defaultThumbnail = new File("public/images/doc.png");
            renderBinary(defaultThumbnail);
        }
        notFound();
    }

    public static void update(long id, @Required Document document) {
        checkAuthenticity();
        User me = Auth.getMe();
        Document check = Document.findById(id);
        notFoundIfNull(check);
        if (check.owner.id == me.id) {
            if (!validation.hasErrors()) {
                check.title = document.title;
                check.category = document.category;
                check.source = document.source;
                check.description = document.description;
                check.tags = document.tags;
                document = check;
                if (document.validateAndSave()) {
                    renderJSON(document);
                }
            }
        } else {
            unauthorized();
        }
    }

}
