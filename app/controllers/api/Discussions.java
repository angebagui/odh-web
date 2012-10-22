package controllers.api;

import jobs.UpdateDocumentCommentCountJob;
import models.Discussion;
import models.Document;
import models.User;
import play.data.validation.Required;
import play.mvc.With;
import controllers.AppController;
import controllers.web.Auth;

@With(ApiController.class)
public class Discussions extends AppController {

    public static void create(@Required Discussion discussion) {
        if (!validation.hasErrors()) {
            User me = getMe();
            discussion.content = discussion.content.trim();
            discussion.user = me;
            if (discussion.validateAndSave()) {
                renderJSON(discussion);
            }
        }
    }

    public static void update(long id, @Required Discussion discussion) {
        checkAuthenticity();
        User me = Auth.getMe();
        Discussion check = Discussion.findById(id);
        notFoundIfNull(check);
        if (check.user.id == me.id) {
            if (!validation.hasErrors()) {
                check.title = discussion.title;
                check.category = discussion.category;
                check.content = discussion.content;
                discussion = check;
                if (discussion.validateAndSave()) {
                    renderJSON(discussion);
                }
            }
        } else {
            unauthorized();
        }
    }
}
