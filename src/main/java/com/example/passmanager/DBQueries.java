package com.example.passmanager;

import java.sql.ResultSet;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBQueries
{

        public DBQueries(){};

        public String addAccount(String platform, String username, String password)
        {
            return "INSERT INTO Accounts (Platform, Username, Password) VALUES ('" + platform + "','" + username + "','" + password +  "')";
        }
        public String removeAccount(String platform, String username, String password)
        {
            return "DELETE FROM Accounts WHERE Platform = '" + platform + "' AND Username = '" + username + "' AND Password = '" + password + "'";
        }
        public String viewAccountInfo(String searchedFor)
        {
            return "SELECT * FROM ACCOUNTS WHERE Platform = '" + searchedFor + "' OR " + "Username = '" + searchedFor + "' OR " + "Password = '" + searchedFor + "'";
        }
        /*public String sortByUsername(){}
        public String sortByPlatform(){}
        public String sortByPassword(){}
        public String sortByDateCreated(){}
        public String sortByLastUpdated(){}
         */
        public String checkForAccount(String username, String password, String platform)
        {
            return "SELECT * FROM ACCOUNTS WHERE Username = '" + username + "' AND Password = '" + password + "' AND Platform = '" + platform + "'";
        }
        public String returnPassword(ResultSet rs)
        {

            try
            {
                while (rs.next())// make this separate function
                {
                    int id = rs.getInt("ID");
                    String platform = rs.getString("Platform");
                    String username = rs.getString("Username");
                    String password = rs.getString("Password");
                    String dateCreated = rs.getString("DateCreated");
                    String lastUpdated = rs.getString("LastUpdated");
                    return password;

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }
        public String resultSetToString(ResultSet rs)
        {

            try
            {
                while (rs.next())// make this separate function
                {
                    int id = rs.getInt("ID");
                    String platform = rs.getString("Platform");
                    String username = rs.getString("Username");
                    String password = rs.getString("Password");
                    String dateCreated = rs.getString("DateCreated");
                    String lastUpdated = rs.getString("LastUpdated");
                    return "Platform: " + platform + "Username: " + username + "Password: " + password + "Date Created: " + dateCreated + "Last Updated: " + lastUpdated;

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }
}
