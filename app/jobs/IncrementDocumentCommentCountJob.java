package jobs;

import models.Comment;
import models.Document;
import play.jobs.Job;

public class IncrementDocumentCommentCountJob extends Job {

    private long commentId;

    public IncrementDocumentCommentCountJob(long commentId) {
        this.commentId = commentId;
    }

    @Override
    public void doJob() {        
        Comment comment = Comment.findById(commentId);        
        if (comment!= null) {
            if (comment.parent != null) {
                comment.parent.increaseRepliesCountAndSave();
            }
            if (comment.document != null) {
                comment.document.incrementCommentCountAndSave();
            }
        }
    }
}
