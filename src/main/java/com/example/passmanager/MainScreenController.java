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

/**
 * This is used as the controller for the main screen of the application. Used for navigation and interacting with the observable list
 *
 */
public class MainScreenController
{
    @FXML
    private ListView<AccountInstance> accountsList;

    private ObservableList<AccountInstance> accounts = FXCollections.observableArrayList();

    /**/
    /**
     * Called automatically by JavaFX after the FXML scene is loaded.
     * We create a list view here that we can interact with which is what initialize is important.
     * We basically are saying hey lets get the accounts from the database, create a list of them and then display them
     * ina list view while we create a click listener that displays a pop-up for that specific account if it is clicked by
     * calling showAccountPopup.
     */
    @FXML
    public void initialize()
    // special method that gets called after the UI has been built, do not have to call this
    // but gets called by Javafx
    {
        accountsList.setItems(accounts);
        loadAccountsFromDB();

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
     * This queries the database to get all the accounts available and when the H2 database returns the result set we
     * format into account objects that we add to an observable list until the result set has no more returns. We then
     * set the observable list into accountsList that then creates cells for each AccountInstance and highlights the
     * account where the primary key ID is 1
     */
    private void loadAccountsFromDB()
    {
        // loads accounts and puts all inside a list
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
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
                                setStyle("-fx-background-color: #ffe680; -fx-font-weight: bold;");
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
     * This creates the pop-up window for when an account is selected in the list view. You have to actions to close the window or
     * delete the account. Session creates a static instance of the password so all controllers can access the main password. We then check if the
     * account is the logged in account and if it is we do not display a delete button. We then wait for user interaction and if the click delete account the account is deleted
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
     * This calls a query from DBQueries to get the account associated with the username and platform given and removes it from the database
     */
    private void deleteAccount(AccountInstance account) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
            DBQueries query = new DBQueries();
            PreparedStatement stmt = conn.prepareStatement(query.removeAccount(account.getPlatform(), account.getUsername()));
            stmt.setString(1, account.getPlatform());
            stmt.setString(2, account.getUsername());
            stmt.executeUpdate();
            conn.close();

            accounts.remove(account);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    /**
     * When the Add Account button is clicked we create a pop-up asking for the necessary information, as long as the information
     * passes the validation format from AddAccountController the account gets created and the listview is updated by calling all the accounts from
     * the database again.
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

            // Refresh list after adding
            accounts.clear();
            loadAccountsFromDB();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
// how does the listview update when i add a new entry during the app



