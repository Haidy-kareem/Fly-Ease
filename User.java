package oopflightbookingsystem.src;

import java.util.ArrayList;

public abstract class User {
    protected int userId;
    protected String username;
    private String password;
    protected String name;
    protected String email;
    protected String contactInfo;
    protected String role;
    protected boolean isActive = true;
    protected static final String DELIMITER = "|";

    protected User(int userId, String username, String password, String name,
                   String email, String contactInfo, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
        this.role = role;
        this.isActive = true;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactInfo() { return contactInfo; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean checkPassword(String passwordToCompare) {
        return this.password != null && this.password.equals(passwordToCompare);
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) this.name = name;
    }

    public void setPassword(String currentPassword, String newPassword) {
        if (this.password.equals(currentPassword)) {
            if (newPassword != null && newPassword.length() >= 6
                    && newPassword.matches(".*[a-zA-Z]+.*")
                    && newPassword.matches(".*[0-9]+.*")) {
                this.password = newPassword;
            }
        }
    }

    public abstract String toFileString();

    protected String getBaseFileString() {
        return String.join(DELIMITER,
                String.valueOf(userId),
                username != null ? username : "",
                password != null ? password : "",
                name != null ? name : "",
                email != null ? email : "",
                contactInfo != null ? contactInfo : "",
                role != null ? role : "",
                String.valueOf(isActive));
    }

    public String getDetailedDescription() {
        return "User ID: " + userId + "\n" +
               "  Username: " + username + "\n" +
               "  Name: " + name + "\n" +
               "  Email: " + email + "\n" +
               "  Contact: " + contactInfo + "\n" +
               "  Role: " + role + "\n" +
               "  Status: " + (isActive ? "Active" : "Inactive") + "\n";
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username=" + username + ", role=" + role + "}";
    }
}
