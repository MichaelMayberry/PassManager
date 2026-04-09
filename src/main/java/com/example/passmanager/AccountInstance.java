package com.example.passmanager;

/**
 * Represents a single stored account entry in the password manager.
 * <p>
 * Each instance holds the platform (service name), username, encrypted password,
 * and the unique database ID for the account.
 * </p>
 */
public class AccountInstance
{
    private String password;
    private String platform;
    private String username;
    private int id;

    /**
     * Constructs an AccountInstance with the given account details.
     *
     * @param id this is the primary key used in the H2 database
     * @param password this password eventually gets encrypted when put into the database and decrypted when taken out
     * @param platform the platform the user wants the associated username and password combo to be used for like GMAIL
     * @param username the name for the account
     */
    public AccountInstance(int id, String password, String platform, String username) {
        this.id = id;
        this.password = password;
        this.platform = platform;
        this.username = username;
    }

    /**
     * Returns the database primary key ID associated with the account
     *
     * @return the account's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns password associated with account, later gets encrypted and decrypted
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets new password for the associated account
     *
     * @param password        the new password to set
     */
    public void setPassword(String password)
    {

            this.password = password;
    }

    /**
     * Returns the platform name for this account.
     *
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets new platform name for this account.
     *
     * @param platform the platform name
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Returns the username for this account.
     *
     * @return the username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets new username for this account.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
