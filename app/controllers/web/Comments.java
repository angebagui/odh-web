package controllers.web;

import java.util.List;

import play.data.validation.Required;
import play.mvc.With;

import models.Comment;
import models.Document;
import controllers.AppController;

@With(WebController.class)
public class Comments extends AppController {
    
    public static void list(@Required String objectType, @Required long objectId, Integer page) {
        if (!validation.hasErrors()) {
            List<Comment> comments = Comment.findByObject(objectType, objectId, page);
            render(comments, objectType, objectId);
        }
    }

}
