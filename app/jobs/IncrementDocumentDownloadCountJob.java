package jobs;

import models.Document;
import play.jobs.Job;

public class IncrementDocumentDownloadCountJob extends Job {

    private long documentId;

    public IncrementDocumentDownloadCountJob(long documentId) {
        this.documentId = documentId;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(this.documentId);
        if (document != null) {
            document.incrementDownloadCountAndSave();
        }
    }
}
