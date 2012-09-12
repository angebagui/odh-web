package services.googleoauth;

import models.User;
import play.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;

public class GoogleOAuthCredentialListener implements CredentialRefreshListener {

    private User me;

    public GoogleOAuthCredentialListener(User me) {
        super();
        this.me = me;
    }

    @Override
    public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenResponse) {
        Logger.error("Failed to refresh token" + "\n User ID : %s" + "\n User Refresh Token : %s", this.me.id, this.me.googleOAuthRefreshToken);
    }

    @Override
    public void onTokenResponse(Credential credential, TokenResponse tokenResponse) {
        this.me.googleOAuthAccessToken = tokenResponse.getAccessToken();
        this.me.save();
    }
}
