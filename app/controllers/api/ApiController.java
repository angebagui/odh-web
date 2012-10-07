package controllers.api;

import java.util.HashMap;
import java.util.Map;

import play.data.validation.Error;
import play.i18n.Messages;
import play.mvc.After;
import play.mvc.Before;
import controllers.AppController;
import controllers.web.Auth;

public class ApiController extends AppController {

    @Before(unless = {
        "api.Categories.list",
        "api.Categories.listDocuments",
        "api.Documents.read",
        "api.Documents.readThumbnail",
        "api.Documents.download",
        "api.Documents.listComments"
    })
    public static void checkAccess() {
        if (getMe() == null) {
            unauthorized();
        }
    }

    @Before
    public static void setDefaultRequestParameters() {
        String pageParam = request.params.get("page");
        if (pageParam != null) {
            int page = Integer.parseInt(pageParam);
            if (page < 1) {
                page = 1;
            }
        }
    }

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
