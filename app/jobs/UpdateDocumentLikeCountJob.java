package jobs;

import models.Document;
import play.jobs.Job;

public class UpdateDocumentLikeCountJob extends Job {

    private long documentId;
    private boolean increment;

    public UpdateDocumentLikeCountJob(long documentId, boolean increment) {
        this.documentId = documentId;
        this.increment = increment;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(documentId);
        if (document!= null) {
            if (document != null) {
                document.updateLikeCountAndSave(this.increment);
            }
        }
    }
}
