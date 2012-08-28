package controllers;

import controllers.admin.Users;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.UserRole;
import play.mvc.With;

@With({Deadbolt.class})
@Restrictions(@Restrict(UserRole.ADMIN))
public class AdminController extends CRUD {

    public static void index() {
        Users.index();
    }

}
