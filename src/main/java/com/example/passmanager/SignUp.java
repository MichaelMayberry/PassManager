package com.example.passmanager;

import java.time.LocalDate;

/**
 * Represents the primary user account created during sign-up
 * Extends AccountInstance with personal details (first name, last name,
 * date of birth) and the salt used to derive the user's AES key.
 * The platform for this account is always MM PassManager and the ID is
 * always 1
 */
public class SignUp extends AccountInstance {

    private String fname;
    private String lname;
    private LocalDate dateOfBirth;
    private String salt;

    /**
     * Constructs a new SignUp account with full user profile information.
     *
     * @param password    the encrypted password for this account
     * @param platform    the platform name MM PassManager
     * @param username    the  username
     * @param fname       the user's first name
     * @param lname       the user's last name
     * @param dateOfBirth the user's date of birth
     * @param salt        the encoded salt
     */
    public SignUp(String password, String platform, String username, String fname, String lname, LocalDate dateOfBirth, String salt)
    {
        super(1, password, platform, username);
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = dateOfBirth;
        this.salt = salt;
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name
     */
    public String getFname()
    {
        return fname;
    }

    /**
     * Sets the user's first name if the string is not empty.
     *
     * @param fname the new first name; ignored if blank
     */
    public void setFname(String fname)
    {
        if(!fname.isEmpty())
        {
            this.fname = fname;
        }
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name
     */
    public String getLname() {
        return lname;
    }

    /**
     * Sets the user's last name if the string is not empty.
     *
     * @param lname the new last name; ignored if blank
     */
    public void setLname(String lname)
    {
        if(!lname.isEmpty())
        {
            this.lname = lname;
        }
    }

    /**
     * Returns the user's date of birth.
     *
     * @return the date of birth as a LocalDate object
     */
    public LocalDate getDateOfBirth()
    {
        return dateOfBirth;
    }

    /**
     * Sets the user's date of birth.
     *
     * @param dateOfBirth the new date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }
}