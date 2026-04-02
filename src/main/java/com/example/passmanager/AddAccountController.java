package com.example.passmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.*;

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

    @FXML
    private void onCancelClick() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

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

        Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
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


        stmt.executeUpdate(query.addAccount(platform, username, password, saltStr));

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
