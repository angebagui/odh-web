package controllers.api;

import jobs.UpdateDocumentCommentCountJob;
import models.Comment;
import models.User;
import play.data.validation.Required;
import play.mvc.With;
import controllers.AppController;

@With(ApiController.class)
public class Comments extends AppController {


    public static void create(@Required Comment comment) {
        checkAuthenticity();
        if (!validation.hasErrors()) {
            User me = getMe();
            comment.content = comment.content.trim();
            comment.author = me;
            if (comment.validateAndSave()) {
                new UpdateDocumentCommentCountJob(comment.document.id, true).in(10);
                renderJSON(comment);
            }
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        Comment comment = Comment.findById(id);
        notFoundIfNull(comment);
        User me = getMe();
        if (comment.author.id.equals(me.id)) {
            long documentId = comment.document.id;
            comment.delete();
            new UpdateDocumentCommentCountJob(documentId, false).in(10);
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

}
