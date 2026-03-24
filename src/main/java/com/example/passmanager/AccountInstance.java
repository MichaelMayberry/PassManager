package com.example.passmanager;

public class AccountInstance
{
    private String password;
    private String platform;
    private String username;

    public AccountInstance(String password, String platform, String username) {
        this.password = password;
        this.platform = platform;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, int recommendedSize) {
        if (password.length() > recommendedSize) {
            this.password = password;
        }
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
