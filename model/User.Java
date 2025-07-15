//user class that extends Person class 

package model;

import abstract.Person;

public class User extends Person {
    private String password;
    private String role;

    public User(int id, String name, String email, String password, String role) {
        super(id, name, email);
        this.password = password;
        this.role = role;
    }

    @Override
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}