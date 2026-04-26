package com.example.passmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.sql.*;

/**
 * JavaFX controller for AddAccountPupup.
 * <p>
 * Handles account creation withing the app with multiple validations occurring throughout
 * </p>
 */
public class AddAccountController
{
    DBQueries query = new DBQueries();
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField platformField;

    @FXML
    private Label errorLabel;

    /**
     * Creates a standardized and secure way to accept passwords and write them into the database
     * by iterating through the password and checking the length for the requirements
     *
     * @param password the plaintext password to derive a key from
     * @return a String
     */
    private String validatePassword(String password) {
        if (password.length() < 10)
            return "Password must be at least 10 characters.";

        int upper = 0;
        int digits = 0;
        int symbols = 0;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper++;
            else if (Character.isDigit(c)) digits++;
            else if (!Character.isLetter(c)) symbols++;
        }

        if (upper < 1)
        {
            return "Password must contain at least 1 uppercase letter.";
        }
        if (digits < 1) {
            return "Password must contain at least 1 number.";
        }
        if (symbols < 2) {
            return "Password must contain at least 2 symbols.";
        }

        return null;
    }
    /**
     * When the pop-up screen to add an account appears there is a cancel button that closes this pop-up.
     * This specifically finds the username field in the pop-up window and closes it which is considered a Scene -> Window which gets cast as a Stage
     */
    @FXML
    private void onCancelClick()
    {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
    /**
     * Handles - Text Field Inputs and validation, Password Requirements check, Creates a connection to database to call
     * a query to add the account information in. Query returns a result set in which we check if it's empty, if not we know
     * there is an account with ID of 1. We then get the username of the previous query to find the salt with another query.
     * With that salt and the session password we create the key to encrypt with and store it in the database and close the database.
     *
     * @throws Exception for SQL database connection and for encryption algorithm
     */
    @FXML
    private void onAddClick() throws Exception {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String platform = platformField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || platform.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match!");
            return;
        }

        String passwordError = validatePassword(password);
        if (passwordError != null) {
            errorLabel.setText(passwordError);
            return;
        }

        Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
        Statement stmt = conn.createStatement();

        ResultSet ex = stmt.executeQuery(query.getUsernameByID(1)); // both of these
        String returnedUsername = null;
        if (ex.next()) {
            returnedUsername = ex.getString("USERNAME");
        } else {
            errorLabel.setText("No user with ID 1 found");
            return;
        }
        ResultSet rs = stmt.executeQuery(query.getAccountByUsername(returnedUsername, "MM PassManager"));
        // what if user enters platform as MM PassManager
        String saltStr = "";
        if (rs.next()) // are the same logic make a method
        {
            saltStr = rs.getString("RANDOM_SALT");
        }
        else
        {
            errorLabel.setText("NO MAIN ACCOUNT");
            return;
        }


        String encryptedPassword = AES.encrypt(password, Session.getKey());
        stmt.executeUpdate(query.addAccount(platform, username, encryptedPassword, saltStr));

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
