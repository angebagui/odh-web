package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.Table;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;
import play.data.validation.Email;
import play.data.validation.Required;
import services.googleoauth.GoogleUserInfo;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_")
public class User extends BaseModel implements RoleHolder {

    @Required
    public String googleOAuthAccessToken;

    @Required
    public String googleOAuthRefreshToken;

    @Required
    @JsonProperty
    public String googleUserId;

    @Required
    @JsonProperty
    public String name;

    @Required
    @Email
    public String email;

    @JsonProperty
    public String picture;

    @JsonProperty
    public String bio;

    @JsonProperty
    public Integer score;

    @JsonProperty
    public Integer documentCount;

    public ArrayList<String> userRoles;

    public User(String googleUserId, String name, String email, String picture) {
        this.googleUserId = googleUserId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.score = 0;
        this.documentCount = 0;
    }

    public List<? extends Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        if (userRoles != null) {
            for (String role : userRoles) {
                roles.add(new UserRole(role));
            }
        }
        return roles;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static User findByEmail(String email) {
        if (email != null) {
            return User.find("email is ?", email).first();
        } else {
            return null;
        }
    }

    public static User createFromGoogleUserInfo(GoogleUserInfo googleUserInfo) {
        User user = new User(googleUserInfo.getId(), googleUserInfo.getName(), googleUserInfo.getEmail(), googleUserInfo.getPicture());
        return user;
    }

    public static User findByGoogleUserId(String googleUserId) {
        if (googleUserId != null) {
            return User.find("googleUserId is ?", googleUserId).first();
        } else {
            return null;
        }
    }

}

