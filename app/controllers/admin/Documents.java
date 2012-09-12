package controllers.admin;

import models.Document;
import models.UserRole;
import play.mvc.With;
import controllers.CRUD;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

@With({ Deadbolt.class })
@Restrictions(@Restrict(UserRole.ADMIN))
@CRUD.For(Document.class)
public class Documents extends CRUD {

}
