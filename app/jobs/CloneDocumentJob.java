package jobs;

import java.io.IOException;

import com.google.api.services.drive.model.File;

import models.BackgroundJobStatus;
import models.DocumentJobStatus;
import models.Document;
import models.User;
import play.Logger;
import play.jobs.Job;

public class CloneDocumentJob extends Job {

    private long documentJobStatusId;
    private long documentId;
    private long userId;

    public CloneDocumentJob(long documentJobStatusId, long documentId, long userId) {
        this.documentJobStatusId = documentJobStatusId;
        this.documentId = documentId;
        this.userId = userId;
    }

    @Override
    public void doJob() {
        DocumentJobStatus documentJobStatus = DocumentJobStatus.findById(this.documentJobStatusId);
        Document document = Document.findById(this.documentId);
        User user = User.findById(this.userId);
        if ((documentJobStatus != null) && (document != null) && (user != null)) {
            try {
                Logger.info("Start : clone document job");
                File copiedFile = document.cloneForUser(user);
                if (copiedFile != null) {
                    documentJobStatus.result = copiedFile.getAlternateLink();
                    documentJobStatus.status = BackgroundJobStatus.Status.SUCCESS;
                    documentJobStatus.save();
                }
            } catch (IOException ex) {
                Logger.error("Error during document copy operation to Google Drive : %s", ex.getMessage());
                documentJobStatus.status = BackgroundJobStatus.Status.FAIL;
                documentJobStatus.save();
            } finally {
                Logger.info("End : clone document job");
            }
        }
    }
}
