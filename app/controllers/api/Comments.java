package controllers.api;

import jobs.IncrementDocumentCommentCountJob;
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
            comment.isCensored = false;            
            if (comment.validateAndSave()) {
                new IncrementDocumentCommentCountJob(comment.id).in(10);
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
            comment.isDeleted = true;
            comment.save();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }
    
    public static void listReplies(long id, int page) {
        Comment comment = Comment.findById(id);
        notFoundIfNull(comment);
        renderJSON(Comment.findByParent(comment.id, page));
    }
    
}
