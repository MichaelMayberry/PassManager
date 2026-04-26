package com.example.passmanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Controller for the main dashboard screen of the application.
 * This is where we manage alerts, generated passwords, password strength tester, and encryption of passwords
 */
public class MainScreenController
{
    @FXML
    private ListView<AccountInstance> accountsList;

    @FXML
    private ListView<String> alertsList;

    @FXML
    private javafx.scene.control.TextField generatedPasswordField;

    private ObservableList<AccountInstance> accounts = FXCollections.observableArrayList();
    private ObservableList<String> alerts = FXCollections.observableArrayList();

    /**/
    /**
     * Called automatically by JavaFX after the FXML scene is loaded.
     * This is what is used to call all the methods we need when the main screen is loaded.
     * Also sets the color red for alerts.
     */
    @FXML
    public void initialize()
    // special method that gets called after the UI has been built, do not have to call this
    // but gets called by Javafx
    {
        accountsList.setItems(accounts);
        alertsList.setItems(alerts);
        alertsList.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("Duplicate")) {
                        setStyle("-fx-text-fill: #ef4444;");
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });
        loadAccountsFromDB();
        refreshAlerts();

        // lambda that allows us to have an anonymous function that gets called
        // every single time the user interacts with the list
        // kind think of it as always listening and even just specifies what we want to do if the click happens
        accountsList.setOnMouseClicked(event -> {
            // getSelectionModel returns the object that was clicked on, get selected item pulls our object account instance
            // and puts it into selected, so if selected is null when we show accountPopUp nothing is their
            // selected != null is used to avoid nullPointerException
            AccountInstance selected = accountsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showAccountPopup(selected);
            }

        });
    }
    /**
     * We call a query from H2 database to populate the Accounts list view
     * We apply a gold highlight to the master password  while putting all the following passwords into
     * account objects  to which we then add to the listview
     */
    private void loadAccountsFromDB()
    {
        // loads accounts and puts all inside a list
        try {
            Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID, Platform, Username, Password FROM Accounts");

            while (rs.next())
            {
                int id = rs.getInt("ID");
                String platform = rs.getString("Platform");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                accounts.add(new AccountInstance(id, password, platform, username));
                // this is a while loop that allows us to get every account in the database, and we add a new account instance object
                // into accounts with this information since the SELECT statement only has 3 columns
            }

            accountsList.setItems(accounts);
            // this sets the items in the observable list we instantiated above

            // says we want to do a listview of accountInstances
            // set cell factory allows us to override how we want each row in our list view
            // to be shown the lv is just there because javaFX requires it
            // ListCell is just an individual part of the list view, its each account instance we have in the list view
            // that's why we return it
            // this calls the lambda method every single time for each individual item in the accountsList list
            accountsList.setCellFactory(lv -> {

                return new javafx.scene.control.ListCell<AccountInstance>() {
                    // We are overriding the updateItem method that is in ListCell, this method is the one that controls
                    // what is only the list cell, happens when we first loads and everytime we scroll
                    // the boolean is used as a way to check if the list is empty or not
                    @Override
                    protected void updateItem(AccountInstance item, boolean empty) {
                        //this fixes the problem of us having the "Ghosting text" which was old text that was not supposed to be there
                        super.updateItem(item, empty);

                        // this simply checks if the cell was null or empty and if it is to not instantiate the text
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        }
                        // other instantiate the text with the correct
                        else {
                            setText(item.getPlatform() + " — " + item.getUsername());
                            if (item.getId() == 1) {
                                setStyle("-fx-background-color: #ffe680;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
            });

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the alerts list, runs all alert checks, and shows No alerts if not conditions are met/
     * All callers use this instead of calling individual checks directly.
     */
    private void refreshAlerts() {
        alerts.clear();
        refreshAlerts();
        checkForReusedUsernames();
        checkForDuplicatePasswords();
        if (alerts.isEmpty()) {
            alerts.add("No alerts.");
        }
    }

    /**
     * Decrypts every password in the database and scans for duplicates.
     * We use an embedded for loop and 2 array lists to go through each password in the database.
     * We also have another array list that is keeping track of which account we are actively looking at.
     * If any 2 passwords are the same we create an alert stating both platforms and usernames where the passwords match
     */
    private void checkForDuplicatePasswords() {
        try {
            Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Platform, Username, Password FROM Accounts");

            ArrayList<String> decryptedPasswords = new ArrayList<>();
            ArrayList<String> accountLabels = new ArrayList<>();

            while (rs.next()) {
                String platform = rs.getString("Platform");
                String username = rs.getString("Username");
                String encryptedPassword = rs.getString("Password");
                try {
                    decryptedPasswords.add(AES.decrypt(encryptedPassword, Session.getKey()));
                    accountLabels.add(platform + " (" + username + ")");
                } catch (Exception e) {
                    continue;
                }
            }
            conn.close();

            boolean[] reported = new boolean[decryptedPasswords.size()];

            for (int i = 0; i < decryptedPasswords.size(); i++)
            {
                if (reported[i]) continue;
                ArrayList<String> group = new ArrayList<>();
                group.add(accountLabels.get(i));
                for (int j = i + 1; j < decryptedPasswords.size(); j++)
                {
                    if (decryptedPasswords.get(i).equals(decryptedPasswords.get(j)))
                    {
                        group.add(accountLabels.get(j));
                        reported[j] = true;
                    }
                }
                if (group.size() > 1) {
                    alerts.add("Duplicate password: " + String.join(", ", group));
                    reported[i] = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Similar to the checkForReusedPasswords we have an embedded for loops with 2 separate arraylists
     * keeping track of where we are in checking.
     *
     */
    private void checkForReusedUsernames() {
        try {
            Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Platform, Username FROM Accounts");

            ArrayList<String> usernames = new ArrayList<>();
            ArrayList<String> platforms = new ArrayList<>();

            while (rs.next()) {
                usernames.add(rs.getString("Username"));
                platforms.add(rs.getString("Platform"));
            }
            conn.close();

            boolean[] reported = new boolean[usernames.size()];

            for (int i = 0; i < usernames.size(); i++) {
                if (reported[i]) continue;
                ArrayList<String> group = new ArrayList<>();
                group.add(platforms.get(i));
                for (int j = i + 1; j < usernames.size(); j++) {
                    if (usernames.get(i).equals(usernames.get(j))) {
                        group.add(platforms.get(j));
                        reported[j] = true;
                    }
                }
                if (group.size() > 1) {
                    alerts.add("Reused username: " + String.join(", ", group) + " (" + usernames.get(i) + ")");
                    reported[i] = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This calls a popup when the accounts listview is clicked on and decrypts the password from the database
     *
     * @param account the {@link AccountInstance} whose details should be displayed
     */

    private void showAccountPopup(AccountInstance account) {
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.LEFT);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Account Details");
        dialog.setHeaderText(account.getPlatform());

        String displayPassword;
        try {
            displayPassword = AES.decrypt(account.getPassword(), Session.getKey());
        } catch (Exception e) {
            displayPassword = account.getPassword();
        }

        DialogPane pane = dialog.getDialogPane();
        pane.setContentText("Username: " + account.getUsername() + "\n" + "Platform: " + account.getPlatform() + "\n" + "Password: " + displayPassword);
        if (account.getId() == 1) {
            pane.getButtonTypes().add(ButtonType.CLOSE);
        } else {
            pane.getButtonTypes().addAll(deleteButton, ButtonType.CLOSE);
        }

        dialog.showAndWait().ifPresent(result -> {
            if (result == deleteButton) {
                deleteAccount(account);
            }
        });
    }
    /**
     * This calls a query from DBQueries to get the account associated with the account Instance
     *
     *
     * @param account the account Instance to delete
     */
    private void deleteAccount(AccountInstance account) {
        try {
            Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
            DBQueries query = new DBQueries();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query.removeAccount(account.getPlatform(), account.getUsername()));
            conn.close();

            accounts.remove(account);
            refreshAlerts();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    /**
     * When the Add Account button is clicked we create a pop-up asking for the necessary information, as long as the
     * information passes the validation format from AddAccountController the account gets created and the listview is updated by calling all
     * the accounts from the database again.
     */
    @FXML
    private void onAddAccountClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/passmanager/add-account-popup.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Account");
            popupStage.setScene(new Scene(loader.load()));
            popupStage.showAndWait();

            // Refresh list and alerts after adding
            accounts.clear();
            loadAccountsFromDB();
            refreshAlerts();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We frist create a string that goes 3 uppercase letters, 5 lowercase letters, 3 digits then 3 symbols.
     * Then in the while loop since that is only 14 characters we add 6 more characters from all. We then use a
     * Shuffler to shuffle the characters and a string builder to append all the chars in the array list.
     */
    @FXML
    private void onGeneratePasswordClick() {
        String upper   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower   = "abcdefghijklmnopqrstuvwxyz";
        String digits  = "0123456789";
        String symbols = "!@#$%^&*";
        String all     = upper + lower + digits + symbols;

        java.security.SecureRandom random = new java.security.SecureRandom();
        ArrayList<Character> chars = new ArrayList<>();

        for (int i = 0; i < 3; i++)
        {
            chars.add(upper.charAt(random.nextInt(upper.length())));
        }
        for (int i = 0; i < 5; i++)
        {
            chars.add(lower.charAt(random.nextInt(lower.length())));
        }
        for (int i = 0; i < 3; i++)
        {
            chars.add(digits.charAt(random.nextInt(digits.length())));
        }
        for (int i = 0; i < 3; i++)
        {
            chars.add(symbols.charAt(random.nextInt(symbols.length())));
        }

        while (chars.size() < 20)
        {
            chars.add(all.charAt(random.nextInt(all.length())));
        }

        Collections.shuffle(chars, random);

        StringBuilder sb = new StringBuilder();
        for (char c : chars)
        {
            sb.append(c);
        }
        generatedPasswordField.setText(sb.toString());
    }

    /**
     * Copies the generated password to the clipboard
     */
    @FXML
    private void onCopyPasswordClick()
    {
        String password = generatedPasswordField.getText();
        if (!password.isEmpty())
        {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(password);
            clipboard.setContent(content);
        }
    }

    /**
     * Opens the password-strength-popup.fxml
     */
    @FXML
    private void onStrengthCheckClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/passmanager/password-strength-popup.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Password Strength Checker");
            popupStage.setScene(new Scene(loader.load()));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

