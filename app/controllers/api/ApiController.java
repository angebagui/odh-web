package controllers.api;

import java.util.HashMap;
import java.util.Map;

import play.data.validation.Error;
import play.i18n.Messages;
import play.mvc.After;
import controllers.AppController;

public class ApiController extends AppController {

    /**
     * Translates and sends error validation messages as a JSON array if any.
     */
    @After
    public static void renderValidationErrors() {
        if (validation.hasErrors()) {
            response.status = 400;
            Map<String, String> errorsMap = new HashMap();
            for (Error error : validation.errors()) {
                errorsMap.put(error.getKey(), Messages.get(error.message()));
            }
            renderJSON(errorsMap);
        }
    }
}
