package services.googleoauth;

import utils.Labels;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import play.Play;

public class GoogleOAuthConfig {

    public static final String ACCESS_TOKEN_END_POINT_PROPERTY_NAME = "accessTokenEndPoint";
    public static final String CODE_END_POINT_PROPERTY_NAME = "codeEndPoint";
    public static final String USER_INFO_END_POINT_PROPERTY_NAME = "userInfoEndPoint";
    public static final String CONF_PROPERTY_NAME_PREFIX = "google.oAuth.";
    private static final String[] CONF_PROPERTIES_NAMES = {
        Labels.CLIENT_ID,
        Labels.CLIENT_SECRET,
        Labels.SCOPE,
        Labels.REDIRECT_URI,
        ACCESS_TOKEN_END_POINT_PROPERTY_NAME,
        CODE_END_POINT_PROPERTY_NAME,
        USER_INFO_END_POINT_PROPERTY_NAME
    };
    
    private static Map<String, String> properties = null;

    public static void loadProperties() {

        properties = new HashMap<String, String>();
        for (String propertyName : CONF_PROPERTIES_NAMES) {
            String fullPropertyName = CONF_PROPERTY_NAME_PREFIX + propertyName;
            String property = Play.configuration.getProperty(fullPropertyName);
            if (property != null) {
                properties.put(propertyName, property);
            } else {
                throw new RuntimeException("Please set " + fullPropertyName + " in application.conf");
            }
        }
    }

    public static String getProperty(String propertyName) {
        if (properties == null) {
            loadProperties();
        }
        if (Arrays.asList(CONF_PROPERTIES_NAMES).contains(propertyName)) {
            return properties.get(propertyName);
        } else {
            throw new RuntimeException("Invalid Google oAuth Config property : " + propertyName);
        }
    }

    public static String getAccessTokenEndPoint() {
        return getProperty(ACCESS_TOKEN_END_POINT_PROPERTY_NAME);
    }

    public static String getUserInfoEndPoint() {
        return getProperty(USER_INFO_END_POINT_PROPERTY_NAME);
    }

    public static String getClientId() {
        return getProperty(Labels.CLIENT_ID);
    }

    public static String getClientSecret() {
        return getProperty(Labels.CLIENT_SECRET);
    }

    public static String getCodeEndPoint() {
        return getProperty(CODE_END_POINT_PROPERTY_NAME);
    }

    public static String getScopes() {
        return getProperty(Labels.SCOPE);
    }

    public static List<String> getScopesAsArray() {
        return Arrays.asList(getScopes().split(" "));
    }

    public static String getRedirectUri() {
        return getProperty(Labels.REDIRECT_URI);
    }
}
