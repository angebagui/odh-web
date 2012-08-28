package services.googleoauth;

import utils.Labels;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import java.io.IOException;
import models.User;
import play.libs.URLs;
import play.libs.WS;
import play.libs.WS.WSRequest;

public class GoogleOAuth {

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    public static String buildCodeRequestUrl() {
        String codeRequestUrl = GoogleOAuthConfig.getCodeEndPoint();
        codeRequestUrl = URLs.addParam(codeRequestUrl, Labels.CLIENT_ID, GoogleOAuthConfig.getClientId());
        codeRequestUrl = URLs.addParam(codeRequestUrl, Labels.REDIRECT_URI, GoogleOAuthConfig.getRedirectUri());
        codeRequestUrl = URLs.addParam(codeRequestUrl, Labels.SCOPE, GoogleOAuthConfig.getScopes());
        codeRequestUrl = URLs.addParam(codeRequestUrl, Labels.RESPONSE_TYPE, Labels.CODE);
        codeRequestUrl = URLs.addParam(codeRequestUrl, Labels.ACCESS_TYPE, Labels.OFFLINE);
        return codeRequestUrl;
    }

    public static Drive buildDriveServiceForUser(User user) throws IOException {
        GoogleCredential credential = buildCredentialForUser(user);
        credential.refreshToken();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
    }

    public static GoogleCredential buildCredentialForUser(User me) {
        String clientId = GoogleOAuthConfig.getClientId();
        String clientSecret = GoogleOAuthConfig.getClientSecret();

        GoogleCredential.Builder credentialBuilder = new GoogleCredential.Builder();
        credentialBuilder.setClientSecrets(clientId, clientSecret);
        credentialBuilder.setTransport(HTTP_TRANSPORT);
        credentialBuilder.setJsonFactory(JSON_FACTORY);
        credentialBuilder.addRefreshListener(new GoogleOAuthCredentialListener(me));

        GoogleCredential credential = credentialBuilder.build();
        credential.setAccessToken(me.googleOAuthAccessToken);
        credential.setRefreshToken(me.googleOAuthRefreshToken);

        return credential;
    }

    public static GoogleOAuthTokens askForOAuthTokens(String code) throws IOException {
        WSRequest tokensRequest = WS.url(GoogleOAuthConfig.getAccessTokenEndPoint());
        tokensRequest.setParameter(Labels.CLIENT_ID, GoogleOAuthConfig.getClientId());
        tokensRequest.setParameter(Labels.CLIENT_SECRET, GoogleOAuthConfig.getClientSecret());
        tokensRequest.setParameter(Labels.REDIRECT_URI, GoogleOAuthConfig.getRedirectUri());
        tokensRequest.setParameter(Labels.CODE, code).setParameter(Labels.GRANT_TYPE, Labels.AUTHORIZATION_CODE);
        WS.HttpResponse tokensResponse = tokensRequest.post();

        if (tokensResponse.getStatus() == 200) {
            return new Gson().fromJson(tokensResponse.getJson(), GoogleOAuthTokens.class);
        } else {
            throw new IOException(String.format(
                    "Error during Tokens Request :" +
                    "\n Response Status : %s " +
                    "\n Response Text : %s",
                    tokensResponse.getStatus(),
                    tokensResponse.getString()));
        }
    }

    public static GoogleUserInfo askForUserInfo(GoogleOAuthTokens googleOAuthTokens) throws IOException {
        WSRequest userInfoRequest = WS.url(GoogleOAuthConfig.getUserInfoEndPoint());
        userInfoRequest.setParameter(Labels.ACCESS_TOKEN, googleOAuthTokens.getAccessToken());
        WS.HttpResponse userInfoResponse = userInfoRequest.get();

        if (userInfoResponse.getStatus() == 200) {
            return new Gson().fromJson(userInfoResponse.getJson(), GoogleUserInfo.class);
        } else {
            throw new IOException(String.format(
                    "Error during User Info Request :" +
                    "\n Response Status : %s " +
                    "\n Response Text : %s",
                    userInfoResponse.getStatus(),
                    userInfoResponse.getString()));
        }
    }
}

