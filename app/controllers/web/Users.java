package controllers.web;

import models.User;
import play.mvc.With;
import controllers.AppController;

@With(WebController.class)
public class Users extends AppController {

	public static void read(long id) {
		User user = User.findById(id);
		notFoundIfNull(user);
		render(user);
	}
}
