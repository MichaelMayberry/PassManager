package com.example.passmanager;

import java.time.LocalDate;

public class SignUp extends AccountInstance {

    private String fname;
    private String lname;
    private LocalDate dateOfBirth;

    public SignUp(String password, String platform, String username, String fname, String lname, LocalDate dateOfBirth)
    {
        super(password, platform, username);
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFname()
    {
        return fname;
    }

    public void setFname(String fname)
    {
        if(!fname.isEmpty())
        {
            this.fname = fname;
        }
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname)
    {
        if(!lname.isEmpty())
        {
            this.lname = lname;
        }
    }

    public LocalDate getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }
}