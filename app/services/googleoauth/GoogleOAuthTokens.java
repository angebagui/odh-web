package services.googleoauth;

import utils.Labels;

import com.google.gson.annotations.SerializedName;

public class GoogleOAuthTokens {

    @SerializedName(Labels.ACCESS_TOKEN)
    private String accessToken;

    @SerializedName(Labels.REFRESH_TOKEN)
    private String refreshToken;

    public GoogleOAuthTokens(String accessToken) {
        this.accessToken = accessToken;
    }

    public GoogleOAuthTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
