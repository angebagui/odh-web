package jobs;

import models.Document;
import play.jobs.Job;

public class IncrementDocumentCopyCountJob extends Job {

    private long documentId;

    public IncrementDocumentCopyCountJob(long documentId) {
        this.documentId = documentId;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(this.documentId);
        if (document != null) {
            document.incrementCopyCountAndSave();
        }
    }
}
