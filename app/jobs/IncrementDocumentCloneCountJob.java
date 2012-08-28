package jobs;

import models.Document;
import play.jobs.Job;

public class IncrementDocumentCloneCountJob extends Job {

    private long documentId;

    public IncrementDocumentCloneCountJob(long documentId) {
        this.documentId = documentId;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(this.documentId);
        if (document != null) {
            document.incrementCloneCount();
        }
    }
}
