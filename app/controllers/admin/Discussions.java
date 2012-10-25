package controllers.admin;

import models.Discussion;
import models.UserRole;
import play.mvc.With;
import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

@With({ Deadbolt.class })
@Restrictions(@Restrict(UserRole.ADMIN))
@CRUD.For(Discussion.class)
public class Discussions extends CRUD {

}
