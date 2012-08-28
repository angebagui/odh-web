package controllers;

import controllers.deadbolt.DeadboltHandler;
import controllers.deadbolt.ExternalizedRestrictionsAccessor;
import controllers.deadbolt.RestrictedResourcesHandler;
import controllers.web.Auth;
import models.deadbolt.RoleHolder;
import play.Logger;
import play.mvc.Controller;

/**
 * Controller that checks Users (AdminController) roles.
 *
 * @author Regis Bamba
 * @since 3/7/12
 */
public class RolesHandler extends Controller implements DeadboltHandler {

    /**
     * Executed before each action that is restricted.
     * Redirects to login if no user session is present.
     */
    public void beforeRoleCheck() {
        if (request.action.contains("admin")) {
            if (Auth.getMe() == null) {
                flash.error("You are not authorized to access this page.");
                redirect("/");
            }
        }
    }

    /**
     * Gets the current user in the session;
     *
     * @return Admin record.
     */
    public RoleHolder getRoleHolder() {
        return Auth.getMe();
    }

    /**
     * Executed when a user does not have access to a page.
     *
     * @param controllerClassName the name of the controller access was denied to
     */
    public void onAccessFailure(String controllerClassName) {
        flash.error("You do not have access to that page.");
        redirect("/");
    }


    public RestrictedResourcesHandler getRestrictedResourcesHandler() {
        return null;
    }

    public ExternalizedRestrictionsAccessor getExternalizedRestrictionsAccessor() {
        return null;
    }

}
