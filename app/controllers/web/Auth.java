package controllers.web;

import java.io.IOException;
import java.util.ArrayList;

import models.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Util;
import play.mvc.With;
import services.googleoauth.GoogleOAuth;
import services.googleoauth.GoogleOAuthTokens;
import services.googleoauth.GoogleUserInfo;
import controllers.AppController;

@With(WebController.class)
public class Auth extends AppController {

    @Util
    public static String getOAuthRedirectUriHost() {
        String redirectHost = request.host;
        if (request.secure) {
            redirectHost = "https://" + redirectHost; 
        } else {
            redirectHost = "http://" + redirectHost;
        }
        return redirectHost;
    }

    public static void googleCode() {
        redirect(GoogleOAuth.buildCodeRequestUrl(getOAuthRedirectUriHost()));
    }

    public static void googleToken(String code) {
        if (code != null) {
            try {
                GoogleOAuthTokens googleOAuthTokens = GoogleOAuth.askForOAuthTokens(code, getOAuthRedirectUriHost());
                GoogleUserInfo googleUserInfo = GoogleOAuth.askForUserInfo(googleOAuthTokens);

                User me = User.findByGoogleUserId(googleUserInfo.getId());
                if (me == null) {
                    me = User.createFromGoogleUserInfo(googleUserInfo);
                }

                me.googleOAuthAccessToken = googleOAuthTokens.getAccessToken();
                if (googleOAuthTokens.getRefreshToken() != null) {
                    me.googleOAuthRefreshToken = googleOAuthTokens.getRefreshToken();
                }

                User checkUser = User.find("").first();
                if (checkUser == null) {
                    me.userRoles = new ArrayList<String>();
                    me.userRoles.add("admin");
                }
                me.save();
                session.put("me.id", me.id);
                flash.success(Messages.get("auth.login.success", me.name));
                flash.keep();
            } catch (IOException e) {
                flash.error(Messages.get("auth.oauth.error"));
                Logger.error("Error during sign in with Google proccess : " + "\n Message : ", e.getMessage());
                redirect("/");
            }
            if (session.get("next") == null) {
                redirect("/");
            } else {
                String next = session.get("next");
                session.remove("next");
                redirect(next);
            }
        }
    }

    public static void logout() {
        checkAuthenticity();
        User me = Auth.getMe();
        if (me != null) {
            session.remove("me.id");
            flash.success(Messages.get("auth.logout.success", me.name));
        }
        redirect("/");
    }
}
