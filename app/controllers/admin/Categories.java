package controllers.admin;

import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.Category;
import models.UserRole;
import play.mvc.With;

@With({Deadbolt.class})
@Restrictions(@Restrict(UserRole.ADMIN))
@CRUD.For(Category.class)
public class Categories extends CRUD {

}
