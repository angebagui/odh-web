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
            // TODO : Need to add validation rule in Comment model that checks that comment.objectType is either "document" or "comment".
            if (comment.validateAndSave()) {
                comment.updateCountForObject(true);
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
            comment.updateCountForObject(false);
            comment.delete();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

}
