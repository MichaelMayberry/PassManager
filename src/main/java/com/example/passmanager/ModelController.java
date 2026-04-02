package com.example.passmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.h2.bnf.context.DbColumn;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;

import java.sql.*;
import java.util.Arrays;
import java.util.Base64;

// model
public class ModelController {
    AccountInstance currentAccount;
    SignUp signUp;
    DBQueries query = new DBQueries();

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField newUsernameField;

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private Label createStatusLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> alertsList;
    @FXML
    private ListView<String> accountsList;


    @FXML
    public void onLoginClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
    }

    @FXML
    public void onCreateAccountClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create-account.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
    }

    @FXML
    public void onCreateAccountSubmit(ActionEvent event) throws Exception
    /*
    This is where we:
    * Create a new account
    * Check if that account exists
    * Make sure all fields are correct for making an account
    * Load into login view when account is created
    * Make sure that only 1 account can use it so check database if empty
    */ {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        LocalDate dob = dobPicker.getValue();
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        byte[] salt = AES.generateSalt();


        if (!password.equals(confirm)) {
            createStatusLabel.setText("Passwords do not match");
            return;
        }

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            createStatusLabel.setText("Fill in all fields");
            return;
        }
        SecretKey key = AES.deriveKey(password, salt);
        String ePassword = AES.encrypt(password, key);

        //DB Query Code
        Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
        Statement stmt = conn.createStatement();

        if (!query.resultSetToString(stmt.executeQuery(query.checkForAccount(username, "MM PassManager"))).isEmpty()) {
            createStatusLabel.setText("Account Already Exists");
            return;
        }
        String eSalt = Base64.getEncoder().encodeToString(salt);
        signUp = new SignUp(ePassword, "MM PassManager", username, firstName, lastName, dob, eSalt);
        stmt.execute(query.addAccount(signUp.getPlatform(), signUp.getUsername(), signUp.getPassword(), eSalt));

        //New Scene loading code
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        conn.close();
    }

    @FXML
    protected void onLoginSubmit(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(query.getAccountByUsername(username, "MM PassManager"));

        if (!rs.next()) {
            statusLabel.setText("Invalid Login Credentials");
            return;
        }


        String storedEncryptedPassword = rs.getString("PASSWORD");
        String saltStr = rs.getString("RANDOM_SALT");
        byte[] salt = Base64.getDecoder().decode(saltStr);

        SecretKey key = AES.deriveKey(password, salt);

        String decryptedPassword = AES.decrypt(storedEncryptedPassword, key);

        if (!decryptedPassword.equals(password)) {
            statusLabel.setText("Invalid Login Credentials");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-screen.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);

        conn.close();
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("first-scene.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
    }
}

