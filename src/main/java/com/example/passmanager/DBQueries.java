package com.example.passmanager;

import javax.crypto.SecretKey;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * This class is used to have a place where all SQL statements passed to the backend can live, although these are susceptible to SQL injections
 * the next scope will be to change that
 */
public class DBQueries
{

        /** Constructs a new DBQueries instance. */
        public DBQueries(){};

        /**
         * Returns an INSERT statement to add a new account record.
         *
         * @param platform the platform/service name
         * @param username the account username
         * @param password the encrypted password
         * @param salt     the Base64-encoded salt used during encryption
         * @return a SQL INSERT string for the Accounts table
         * @throws Exception if the query cannot be constructed
         */
        public String addAccount(String platform, String username, String password, String salt) throws Exception
        {
            return "INSERT INTO Accounts (Platform, Username, Password, RANDOM_SALT) VALUES ('" + platform + "','" + username + "','" + password +  "','" + salt + "')";
        }
        /**
         * Returns a DELETE statement to remove an account matching the platform and username.
         *
         * @param platform the platform name to match
         * @param username the username to match
         * @return a SQL DELETE string for the Accounts table
         * @throws Exception if the query cannot be constructed
         */
        public String removeAccount(String platform, String username) throws Exception
        {
            return "DELETE FROM Accounts WHERE Platform = '" + platform + "' AND Username = '" + username +  "'";
        }
        /**
         * Returns a SELECT statement to find accounts matching a platform, username, or password.
         *
         * @param searchedFor the value to search across Platform, Username, and Password columns
         * @return a SQL SELECT string for the Accounts table
         * @throws Exception if the query cannot be constructed
         */
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
        /**
         * Returns a SELECT statement to check whether an account already exists
         * for the given username and platform.
         *
         * @param username the username to look up
         * @param platform the platform name to match
         * @return a SQL SELECT string for the Accounts table
         * @throws Exception if the query cannot be constructed
         */
        public String checkForAccount(String username,  String platform) throws Exception {

            return "SELECT * FROM ACCOUNTS WHERE Username = '" + username + "' AND Platform = '" + platform + "'";
        }
        /**
         * Returns a SELECT statement to retrieve the PASSWORD and RANDOM_SALT
         * for an account matching the given username and platform.
         *
         * @param username the username to look up
         * @param platform the platform name to match
         * @return a SQL SELECT string returning PASSWORD and RANDOM_SALT columns
         * @throws Exception if the query cannot be constructed
         */
        public String getAccountByUsername(String username, String platform) throws Exception
        {

            return "SELECT PASSWORD, RANDOM_SALT FROM ACCOUNTS WHERE Username = '" + username + "' AND Platform = '" + platform + "'";
        }
        /**
         * Returns a SELECT statement to retrieve the USERNAME for a given account ID.
         *
         * @param ID the account ID to look up
         * @return a SQL SELECT string returning the USERNAME column
         * @throws Exception if the query cannot be constructed
         */
        public String getUsernameByID(int ID) throws Exception
        {
            return "SELECT USERNAME FROM ACCOUNTS WHERE ID = '" + ID + "'";
        }

        /**
         * Reads the first row of a result set and returns its contents as a
         * formatted string.
         *
         * @param rs the result set to read from
         * @return a string containing ID, Platform, Username, Password, and Salt from the first
         *         row, or an empty string if the result set is empty
         */
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
