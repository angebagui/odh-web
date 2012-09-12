package jobs;

import java.io.IOException;

import models.Document;
import play.Logger;
import play.jobs.Job;

public class FetchDocumentThumbnailJob extends Job {

    private long documentId;

    public FetchDocumentThumbnailJob(long documentId) {
        this.documentId = documentId;
    }

    @Override
    public void doJob() {
        Document document = Document.find("id is ?", this.documentId).first();
        if (document != null) {
            try {
                document.fetchThumbnailFromGoogleDriveAndSave();
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
            }
        }
    }
}
