package controllers.web;

import models.Category;
import play.mvc.Before;
import controllers.AppController;

public class WebController extends AppController {

    @Before
    public static void addViewArgs() {
        renderArgs.put("categories", Category.all().fetch());
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
        "web.WebController.updateLanguage",
    })
    public static void checkAccess() {
        if (getMe() == null) {
            flash.error("auth.login.needed");
            session.put("next", request.url);
            Auth.googleCode();
        }
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