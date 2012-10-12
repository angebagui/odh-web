package controllers.web;

import java.util.List;

import models.Category;
import models.Document;
import models.User;
import play.mvc.Before;
import play.mvc.Util;
import controllers.AppController;

public class WebController extends AppController {

    @Before
    public static void addViewArgs() {
        List <Category> categories = Category.find("order by name asc").fetch();
        renderArgs.put("categories", categories);
        renderArgs.put("me", getMe());
    }

    @Before(unless = {
        "web.Auth.googleCode",
        "web.Auth.googleToken",
        "web.Auth.googleOAuth",
        "web.Documents.incrementReadCount",
        "web.Documents.go",
        "web.Documents.list",
        "web.Documents.listClones",
        "web.Documents.read",
        "web.Documents.listComments",
        "web.Users.read",
        "web.WebController.index"
    })
    public static void checkAccess() {
        if (getMe() == null) {
            flash.error("auth.login.needed");
            Auth.googleCode(request.url);
        }
    }

    @Before
    public static void setDefaultRequestParameters() {
        String pageParam = request.params.get("page");
        if (pageParam != null) {
            int page = Integer.parseInt(pageParam);
            if (page < 1) {
                request.params.put("page", "1");
            }
        } else {
            request.params.put("page", "1");
        }
    }

    @Util // we are not using it for now
    public static void updateLanguage(String lang, String nextUrl) {
        checkAuthenticity();
        if (lang != null) {
            if (lang.equals("en")) {
                session.put("lang", "en");
            } else {
                session.put("lang", "fr");
            }
        }
        redirect(nextUrl);
    }

    public static void index() {
        List<Document> recentDocuments = Document.find("order by created desc").fetch(4);
        List<User> recentUsers = User.find("order by created desc").fetch(12);
        render(recentDocuments, recentUsers);
    }

    public static void search(String keyword, String searchType) {
        if (searchType != null) {
            if (searchType.equals("documents")) {
                Documents.list(keyword, 0, null, null);
            }
        }
        index();
    }

}