package services.googleoauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import models.User;
import play.Logger;

public class GoogleOAuthCredentialListener implements CredentialRefreshListener {

    private User me;

    public GoogleOAuthCredentialListener(User me) {
        super();
        this.me = me;
    }

    public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
        me.googleOAuthAccessToken = tokenResponse.getAccessToken();
        me.save();
    }

    public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenResponse) {
        Logger.error("Failed to refresh token" +
                "\n User ID : %s" +
                "\n User Refresh Token : %s", me.id, me.googleOAuthRefreshToken);
    }
}
