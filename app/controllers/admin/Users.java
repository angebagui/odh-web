
package controllers.admin;

import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.User;
import models.UserRole;
import play.mvc.With;

@With({Deadbolt.class})
@CRUD.For(User.class)
@Restrictions(@Restrict(UserRole.ADMIN))
public class Users extends CRUD {

}
