package controllers.admin;

import models.User;
import models.UserRole;
import play.mvc.With;
import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

@With({ Deadbolt.class })
@CRUD.For(User.class)
@Restrictions(@Restrict(UserRole.ADMIN))
public class Users extends CRUD {

}
