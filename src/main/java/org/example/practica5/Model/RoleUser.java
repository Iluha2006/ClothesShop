package org.example.practica5.Model;

import org.example.practica5.Service;

public enum RoleUser {
    ADMIN("Администратор"),
    MANAGER("Менеджер"),
    CLIENT("Авторизированный клиент"),
    GUEST("Гость");

    private  String role ;

    RoleUser(String role) {
        this.role=role;
    }

    public String getRole() {
        return role;
    }

    public static RoleUser fromString(String text) {
        if (text == null) return CLIENT;


        switch (text) {
            case "Администратор": return ADMIN;
            case "Менеджер": return MANAGER;
            case "Авторизированный клиент": return CLIENT;
            case "Гость": return GUEST;
            default: return CLIENT;
        }
    }
}