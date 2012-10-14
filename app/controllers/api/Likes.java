package controllers.api;

import jobs.UpdateDocumentLikeCountJob;
import controllers.web.Auth;
import models.Like;
import models.User;
import play.data.validation.Required;

public class Likes extends ApiController {

    public static void create(@Required Like like) {
        checkAuthenticity();
        if (!validation.hasErrors()) {
            User me = Auth.getMe();
            Like check = me.getLikeForObject(like.objectType, like.objectId);
            if (check == null) {
                like.user = me;
                if (like.validateAndSave()) {
                    if (like.objectType.equals("document")) {
                        new UpdateDocumentLikeCountJob(like.objectId, true).now();
                    }
                    renderJSON(like);
                }
            } else {
                renderJSON(check);
            }
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Like like = Like.findById(id);
        notFoundIfNull(like);
        if (like.user.id == me.id) {
            if (like.objectType.equals("document")) {
                new UpdateDocumentLikeCountJob(like.objectId, false).now();
            }
            like.delete();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

    public static void read(@Required String objectType, @Required long objectId) {
        if (!validation.hasErrors()) {
            User me = Auth.getMe();
            Like like = me.getLikeForObject(objectType, objectId);
            if (like != null) {
                renderJSON(like);
            } else {
                notFound();
            }
        }
    }
}
