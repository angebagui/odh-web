package controllers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import models.User;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Util;

public class AppController extends Controller {

    protected static final String COUNT_PARAMETER_LABEL = "count";
    protected static final int COUNT_PARAMETER_DEFAULT_VALUE = 20;
    protected static final int COUNT_PARAMETER_MAX_VALUE = 100;

    protected static ObjectMapper jsonObjectMapper = null;

    @Before
    public static void setLanguage() {
        if (session.get("lang") != null) {
            Lang.set(session.get("lang"));
        } else {
            session.put("lang", "fr");
            Lang.set("fr");
        }
    }

    @Util
    public static User getMe() {
        String meIdString = session.get("me.id");
        if (meIdString != null) {
            return User.findById(Long.parseLong(meIdString));
        } else {
            return null;
        }
    }

    @Util
    public static void renderJSON(Object object) {

        if (jsonObjectMapper == null) {
            jsonObjectMapper = new ObjectMapper();
            jsonObjectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            //jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
        }

        if (Play.mode.isDev() || Play.runingInTestMode()) {
            jsonObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            Logger.setUp("DEBUG");
        }

        try {
            String jsonString = jsonObjectMapper.writeValueAsString(object);
            Logger.debug("Rendering JSON" +
                    "\n --------------------------------- " +
                    "\n %s : %s" +
                    "\n --------------------------------- ",
                    object.getClass().getCanonicalName(), jsonString);
            Controller.renderJSON(jsonString);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
