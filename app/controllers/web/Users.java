package controllers.web;

import java.util.List;

import models.Document;
import models.User;
import play.mvc.With;
import controllers.AppController;

@With(WebController.class)
public class Users extends AppController {

	public static void read(long id) {
		User user = User.findById(id);
		notFoundIfNull(user);
		List<Document> documents = Document.findByUser(user);
		render(user, documents);
	}
}
