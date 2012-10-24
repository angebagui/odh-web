package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;
import play.data.validation.Email;
import play.data.validation.Required;
import services.googleoauth.GoogleUserInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user_")
public class User extends BaseModel implements RoleHolder {

    @JsonProperty
    public String bio;

    @Required
    @Email
    public String email;

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

    @JsonProperty
    public String picture;

    @JsonProperty
    public int karma;

    public ArrayList<String> userRoles;

    public User(String googleUserId, String name, String email, String picture) {
        this.googleUserId = googleUserId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.karma = 0;
    }

    public int addToKarmaAndSave(int addition) {
        this.karma = this.karma + addition;
        this.save();
        return this.karma;
    }

    @Override
    public List<? extends Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        if (this.userRoles != null) {
            for (String role : this.userRoles) {
                roles.add(new UserRole(role));
            }
        }
        return roles;
    }

    public Vote getVoteForObject(String objectType, long objectId) {
        if (this.id != null && objectType != null) {
            return Vote.find("user.id is ? and objectType is ? and objectId is ?", this.id, objectType.toLowerCase(), objectId).first();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static User createFromGoogleUserInfo(GoogleUserInfo googleUserInfo) {
        User user = new User(googleUserInfo.getId(), googleUserInfo.getName(), googleUserInfo.getEmail(), googleUserInfo.getPicture());
        return user;
    }

    public static User findByEmail(String email) {
        if (email != null) {
            return User.find("email is ?", email).first();
        } else {
            return null;
        }
    }

    public static User findByGoogleUserId(String googleUserId) {
        if (googleUserId != null) {
            return User.find("googleUserId is ?", googleUserId).first();
        } else {
            return null;
        }
    }

}
