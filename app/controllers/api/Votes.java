package controllers.api;

import controllers.web.Auth;
import models.Comment;
import models.Document;
import models.Vote;
import models.User;
import play.data.validation.Required;

public class Votes extends ApiController {

    public static void create(@Required Vote vote) {
        checkAuthenticity();
        if (!validation.hasErrors()) {
            User me = Auth.getMe();
            Vote check = me.getVoteForObject(vote.objectType, vote.objectId);
            if (check == null) {
                vote.user = me;
                if (vote.validateAndSave()) {
                    vote.updateCountForObject(true);
                    renderJSON(vote);
                }
            } else {
                renderJSON(check);
            }
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Vote vote = Vote.findById(id);
        notFoundIfNull(vote);
        if (vote.user.id == me.id) {
            vote.updateCountForObject(false);
            Vote _vote = vote;
            vote.delete();
            renderJSON(_vote);
        } else {
            unauthorized();
        }
    }

    public static void read(@Required String objectType, @Required long objectId) {
        if (!validation.hasErrors()) {
            User me = Auth.getMe();
            Vote vote = me.getVoteForObject(objectType, objectId);
            if (vote != null) {
                renderJSON(vote);
            } else {
                notFound();
            }
        }
    }
}
