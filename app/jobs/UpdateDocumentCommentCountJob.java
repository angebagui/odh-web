package jobs;

import models.Document;
import play.jobs.Job;

public class UpdateDocumentCommentCountJob extends Job {

    private long documentId;
    private boolean increment;

    public UpdateDocumentCommentCountJob(long documentId, boolean increment) {
        this.documentId = documentId;
        this.increment = increment;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(documentId);
        if (document!= null) {
            if (document != null) {
                document.updateCommentCountAndSave(this.increment);
            }
        }
    }
}
