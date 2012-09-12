package controllers;

import models.deadbolt.RoleHolder;
import play.i18n.Messages;
import play.mvc.Controller;
import controllers.deadbolt.DeadboltHandler;
import controllers.deadbolt.ExternalizedRestrictionsAccessor;
import controllers.deadbolt.RestrictedResourcesHandler;
import controllers.web.Auth;

public class RolesHandler extends Controller implements DeadboltHandler {

    /**
     * Executed before each action that is restricted. Redirects to login if no
     * user session is present.
     */
    @Override
    public void beforeRoleCheck() {
        AppController.setLanguage();
        if (request.action.contains("admin")) {
            if (Auth.getMe() == null) {
                flash.error(Messages.get("auth.unauthorizedAccess"));
                redirect("/");
            }
        }
    }

    @Override
    public ExternalizedRestrictionsAccessor getExternalizedRestrictionsAccessor() {
        return null;
    }

    @Override
    public RestrictedResourcesHandler getRestrictedResourcesHandler() {
        return null;
    }

    /**
     * Gets the current user in the session;
     * 
     * @return Admin record.
     */
    @Override
    public RoleHolder getRoleHolder() {
        return Auth.getMe();
    }

    /**
     * Executed when a user does not have access to a page.
     * 
     * @param controllerClassName
     *            the name of the controller access was denied to
     */
    @Override
    public void onAccessFailure(String controllerClassName) {
        flash.error(Messages.get("auth.unauthorizedAccess"));
        redirect("/");
    }

}
