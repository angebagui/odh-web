package jobs;

import models.CloneDocumentJobStatus;
import models.Document;
import models.User;
import play.Logger;
import play.jobs.Job;

import java.io.IOException;

public class CloneDocumentJob extends Job {

    private long cloneDocumentJobStatusId;
    private long documentId;
    private long userId;

    public CloneDocumentJob(long cloneDocumentJobStatusId, long documentId, long userId) {
        this.cloneDocumentJobStatusId = cloneDocumentJobStatusId;
        this.documentId = documentId;
        this.userId = userId;
    }

    @Override
    public void doJob() {
        CloneDocumentJobStatus cloneDocumentJobStatus = CloneDocumentJobStatus.findById(this.cloneDocumentJobStatusId);
        Document document = Document.findById(this.documentId);
        User user = User.findById(this.userId);
        if (cloneDocumentJobStatus != null && document != null && user != null) {
            try {
                Logger.info("Starting clone");
                Document clonedDocument = document.copyToGoogleDrive(user);
                cloneDocumentJobStatus.clonedDocumentId = clonedDocument.id;
                cloneDocumentJobStatus.done = true;
                cloneDocumentJobStatus.save();
            } catch(IOException ex) {
                cloneDocumentJobStatus.delete();
                Logger.error("Error during document copy operation to Google Drive : %s", ex.getMessage());
            }
        } else {
            Logger.info("%s", cloneDocumentJobStatus != null);
        }
    }
}
