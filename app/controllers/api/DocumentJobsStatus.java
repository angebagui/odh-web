package controllers.api;

import models.DocumentJobStatus;
import controllers.AppController;
import play.mvc.With;

@With(ApiController.class)
public class DocumentJobsStatus extends AppController {

    public static void read(long id) {
        DocumentJobStatus documentJobStatus = DocumentJobStatus.findById(id);
        notFoundIfNull(documentJobStatus);
        renderJSON(documentJobStatus);
    }
}
