package jobs;

import models.Document;
import play.jobs.Job;

/**
 * Created by IntelliJ IDEA.
 * User: Regis
 * Date: 8/21/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class IncrementDocumentReadCountJob extends Job {

    private long documentId;

    public IncrementDocumentReadCountJob(long documentId) {
        this.documentId = documentId;
    }

    @Override
    public void doJob() {
        Document document = Document.findById(this.documentId);
        if (document != null) {
            document.incrementReadCount();
        }
    }
}
