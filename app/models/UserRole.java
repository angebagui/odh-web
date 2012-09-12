/**
 *
 * @since 3/1/12
 * @author: Regis Bamba
 *
 **/

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

}
