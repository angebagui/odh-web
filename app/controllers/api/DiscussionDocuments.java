package controllers.api;

import models.Discussion;
import models.DiscussionDocument;
import models.Document;
import models.User;
import models.Vote;
import play.data.validation.Required;
import play.mvc.With;
import controllers.AppController;
import controllers.web.Auth;

@With(ApiController.class)
public class DiscussionDocuments extends AppController {

    public static void create(DiscussionDocument discussionDocument) {
        checkAuthenticity();
        notFoundIfNull(discussionDocument);
        User me = getMe();
        discussionDocument.user = me;
        DiscussionDocument check = DiscussionDocument.find("discussion.id is ? and document.id is ?", discussionDocument.discussion.id, discussionDocument.document.id).first();
        if (check != null) {
            renderJSON(check);
        } else if (discussionDocument.validateAndSave()) {
            renderJSON(discussionDocument);
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        DiscussionDocument discussionDocument = DiscussionDocument.findById(id);
        notFoundIfNull(discussionDocument);
        if (discussionDocument.user.id == me.id) {
            discussionDocument.delete();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

}
