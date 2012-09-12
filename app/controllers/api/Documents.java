package controllers.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import jobs.CloneDocumentJob;
import jobs.IncrementDocumentDownloadCountJob;
import models.Category;
import models.Document;
import models.DocumentJobStatus;
import models.ExportLink;
import models.User;
import models.enums.Mime;
import play.Logger;
import play.data.validation.Required;
import play.mvc.With;
import controllers.AppController;
import controllers.web.Auth;

@With(ApiController.class)
public class Documents extends AppController {

    public static void create(@Required Document document) {
        // Logger.info(document.file.getContentType());
        User me = Auth.getMe();
        document.setMime(Mime.parseName(document.file.getContentType()));
        if (validation.valid(document).ok) {
            try {
                document.owner = me;
                document.uploadToGoogleDriveAndSave();
                renderJSON(document);
            } catch (IOException e) {
                Logger.error("Could not insert file on Google Drive :" + "\n\t File Name : %s" + "\n\t File Size : %s" + "\n\t File Owner ID : %s" + "\n\t Exception Message : %s", document.file.getFileName(), document.file.getSize(), me.id, e.getMessage());
                response.status = 500;
                renderJSON(e.getMessage());
            }

        }
    }

    public static void createClone(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Document document = Document.findById(id);
        notFoundIfNull(document);
        if (me != document.owner) {
            DocumentJobStatus documentJobStatus = new DocumentJobStatus();
            documentJobStatus.save();
            new CloneDocumentJob(documentJobStatus.id, document.id, me.id).in(1);
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
            document.isArchived = true;
            document.save();
            if (document.originalDocument != null) {
                document.originalDocument.decreaseCloneCountAndSave();
            }
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
                renderJSON(document);
                // flash.success(Messages.get("document.updateDetails.success"));
            }
        } else {
            unauthorized();
            // flash.error("auth.unauthorizedAccess");
        }
    }

}
