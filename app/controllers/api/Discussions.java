package controllers.api;

import java.util.Date;
import java.util.List;

import jobs.UpdateDocumentCommentCountJob;
import models.Category;
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
public class Discussions extends AppController {

    public static void create(@Required Discussion discussion, Document document) {
        if (!validation.hasErrors()) {
            User me = getMe();
            discussion.content = discussion.content.trim();
            discussion.user = me;
            if (discussion.validateAndSave()) {
                if (document.id != null) {
                    DiscussionDocument discussionDocument = new DiscussionDocument(discussion, document, me);
                    if (discussionDocument.validateAndSave()) {
                        discussion.updateDocumentCountAndSave(true);
                        document.updateDiscussionCountAndSave(true);
                    }
                }
                renderJSON(discussion);
            }
        }
    }

    public static void delete(long id) {
        checkAuthenticity();
        User me = Auth.getMe();
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        if (discussion.user.id == me.id) {
            Vote.deleteAllForDiscussion(discussion.id);
            discussion.delete();
            renderJSON(true);
        } else {
            unauthorized();
        }
    }

    public static void list(String keyword, long categoryId, String order, Integer page) {
        List<Document> documents = Document.search(keyword, categoryId, order, page);
        renderJSON(documents);
    }

    public static void markAsViewed(long id) {
        checkAuthenticity();
        Discussion discussion = Discussion.findById(id);
        if (discussion != null) {
            String sessionKey = String.format("lastDiscussionViewed", id);

            if (session.get(sessionKey) != null) {
                if (session.get(sessionKey) != discussion.id.toString()) {
                    discussion.increaseViewCountAndSave();
                    session.put(sessionKey, discussion.id);
                }
            } else {
                discussion.increaseViewCountAndSave();
                session.put(sessionKey, new Date().getTime());
            }
        } else {
            notFound();
        }
        ok();
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
                check.tags = discussion.tags;
                check.user = me;
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
