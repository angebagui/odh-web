package controllers.web;

import java.util.List;

import models.Category;
import models.Discussion;
import models.DiscussionDocument;
import models.Document;
import models.User;
import models.Vote;

import play.mvc.Before;
import play.mvc.With;

import controllers.AppController;

@With(WebController.class)
public class Discussions extends AppController {

    @Before
    public static void addViewArgs() {
        List <Category> categories = Category.findForDiscussion();
        renderArgs.put("categories", categories);
    }

    public static void add(long documentId) {
        if (documentId > 0) {
            Document document = Document.findById(documentId);
            notFoundIfNull(document);
            renderArgs.put("document", document);
        }
        render();
    }

    public static void edit(long id) {
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        User me = Auth.getMe();
        if (discussion.wasStartedByUser(me)) {
            render(discussion);
        } else {
            go(id);
        }
    }

    public static void go(long id) {
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        view(discussion.id, discussion.slug);
    }

    public static void list(String keyword, long categoryId, String order, Integer page) {
        Category category = Category.findById(categoryId);
        List<Discussion> discussions = Discussion.search(keyword, categoryId, order, page);
        if (request.isAjax()) {
            renderTemplate("web/Discussions/ajax_list.html", discussions, category, keyword, order, page);
        } else {
            render(discussions, category, keyword, order, page);
        }
    }

    public static void view(long id, String slug) {
        Discussion discussion = Discussion.findById(id);
        notFoundIfNull(discussion);
        List<DiscussionDocument> documents = DiscussionDocument.findByDiscussion(discussion.id);
        render(discussion, documents);
    }
}
