package jobs;

import com.google.gson.Gson;
import models.Document;
import models.Thumbnail;
import play.Logger;
import play.jobs.Job;

import java.io.IOException;

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
                document.fetchThumbnailFromGoogleDrive();
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
            }
        }
    }
}
