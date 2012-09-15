package controllers.web;

import java.util.List;

import play.mvc.With;
import controllers.AppController;

import models.Comment;
import models.User;
@With(WebController.class)
public class Comments extends AppController {
    
    public static void listReplies(long id, Integer page) {
        Comment parent = Comment.findById(id);
        notFoundIfNull(parent);
        List<Comment> comments = Comment.findByParent(parent.id, page);
        renderTemplate("web/Documents/listComments.html", comments, parent);
    }
    
}
