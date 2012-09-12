package controllers;

import models.UserRole;
import play.mvc.With;
import controllers.admin.Users;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

@With({ Deadbolt.class })
@Restrictions(@Restrict(UserRole.ADMIN))
public class AdminController extends CRUD {

    public static void index() {
        Users.index();
    }

}
