package org.example.practica5.Model;

public class Role {
    public  int id;
    public RoleUser roleUser;


    public Role(int id, RoleUser roleUser) {
        this.id = id;
        this.roleUser=roleUser;
    }

    public RoleUser getRoleUser() {
        return roleUser;
    }

    public int getId() {
        return id;
    }
}


