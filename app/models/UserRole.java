package models;

import models.deadbolt.Role;

public class UserRole implements Role {

    public String name;

    public UserRole(String name) {
        this.name = name;
    }

    @Override
    public String getRoleName() {
        return this.name;
    }
    
    public final static String ADMIN = "admin";
    public final static String MODERATOR = "moderator";    
    public final static String [] ROLES = {ADMIN, MODERATOR};

}
