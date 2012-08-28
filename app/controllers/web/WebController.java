package controllers.web;

import controllers.AppController;
import models.Category;
import play.i18n.Lang;
import play.mvc.Before;

public class WebController extends AppController {

    @Before(unless = {
        "web.WebController.updateLanguage",
        "web.Auth.googleCode",
        "web.Auth.googleToken",
        "web.Auth.googleOAuth",
        "web.Documents.read",
        "web.Documents.readThumbnail",
        "web.Documents.incrementReadCount",
        "web.Documents.download",
        "web.Documents.go",
        "web.Documents.list",
        "web.Documents.listClones"
    })
    public static void checkAccess() {
        if (getMe() == null) {
            flash.error("auth.login.needed");
            session.put("next", request.url);
            Auth.googleCode();
        }
    }

    @Before
    public static void addViewArgs() {
        renderArgs.put("categories", Category.all().fetch());
        renderArgs.put("me", getMe());
    }

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

}