package controllers.admin;

import models.Comment;
import models.UserRole;
import play.mvc.With;
import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

@With({ Deadbolt.class })
@Restrictions(@Restrict(UserRole.ADMIN))
@CRUD.For(Comment.class)
public class Comments extends CRUD {

}
