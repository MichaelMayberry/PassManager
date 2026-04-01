package com.example.passmanager;

import javax.crypto.SecretKey;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBQueries
{

        public DBQueries(){};

        public String addAccount(String platform, String username, String password, String salt) throws Exception
        {
            return "INSERT INTO Accounts (Platform, Username, Password, RANDOM_SALT) VALUES ('" + platform + "','" + username + "','" + password +  "','" + salt + "')";
        }
        public String removeAccount(String platform, String username, String password) throws Exception
        {
            return "DELETE FROM Accounts WHERE Platform = '" + platform + "' AND Username = '" + username + "' AND Password = '" + password + "'";
        }
        public String viewAccountInfo(String searchedFor) throws Exception
        {
            return "SELECT * FROM ACCOUNTS WHERE Platform = '" + searchedFor + "' OR " + "Username = '" + searchedFor + "' OR " + "Password = '" + searchedFor + "'";
        }
        /*public String sortByUsername(){}
        public String sortByPlatform(){}
        public String sortByPassword(){}
        public String sortByDateCreated(){}
        public String sortByLastUpdated(){}
         */
        public String checkForAccount(String username,  String platform) throws Exception {

            return "SELECT * FROM ACCOUNTS WHERE Username = '" + username + "' AND Platform = '" + platform + "'";
        }
        public String getAccountByUsername(String username, String platform) throws Exception
        {

            return "SELECT PASSWORD, RANDOM_SALT FROM ACCOUNTS WHERE Username = '" + username + "' AND Platform = '" + platform + "'";
        }

        public String returnSalt(ResultSet rs)
        {
            try
            {
                while (rs.next())// make this separate function
                {
                    int id = rs.getInt("ID");
                    String platform = rs.getString("Platform");
                    String username = rs.getString("Username");
                    String password = rs.getString("Password");
                    String salt = rs.getString("Salt");
                    return salt;

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }
        public String returnPassword(ResultSet rs) {
            try {
                if (rs.next()) {
                    return rs.getString("Password");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
                    String salt = rs.getString("RANDOM_SALT");
                    return "Platform: " + platform + "Username: " + username + "Password: " + password + "Salt: " + salt;

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }
}
