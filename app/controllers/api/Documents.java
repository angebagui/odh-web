package controllers.api;

import controllers.AppController;
import controllers.web.Auth;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import jobs.IncrementDocumentDownloadCountJob;
import models.Document;
import models.ExportLink;
import models.User;
import models.enums.Mime;
import play.Logger;
import play.data.validation.Required;
import play.mvc.With;

@With(ApiController.class)
public class Documents extends AppController {

    public static void create(@Required Document document) {
        //Logger.info(document.file.getContentType());
        User me = Auth.getMe();
        document.setMime(Mime.parseName(document.file.getContentType()));
        if (validation.valid(document).ok) {
            try {
                document.owner = me;
                document.uploadToGoogleDrive();
                renderJSON(document);
            } catch (IOException e) {
                Logger.error("Could not insert file on Google Drive :" +
                        "\n\t File Name : %s" +
                        "\n\t File Size : %s" +
                        "\n\t File Owner ID : %s" +
                        "\n\t Exception Message : %s",
                        document.file.getFileName(), document.file.getSize(), me.id, e.getMessage());
                response.status = 500;
                renderJSON(e.getMessage());
            }

        }

        if (!request.isAjax()) {
            renderArgs.put("document", document);
            renderTemplate("web/Documents/add.html");
        }
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

}
