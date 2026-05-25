package org.example.practica5.Model;

public class User {
    private int id;
    private String fullName;
    private String login;
    private String password;
    private RoleUser role;

    public User(int id, String fullName, String login, String password,RoleUser role) {
        this.id = id;
        this.fullName = fullName;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(int id, String name) {
        this.id = id;
        this.fullName=name;
    }

    public User(String fullName, String login, RoleUser role) {
        this.fullName = fullName;
        this.login = login;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public RoleUser getRole() {  return role; }

    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(RoleUser role) { this.role = role; }
}