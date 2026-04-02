package com.example.passmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;

public class MainScreenController
{
    @FXML
    private ListView<AccountInstance> accountsList;

    private ObservableList<AccountInstance> accounts = FXCollections.observableArrayList();

    /**/
    @FXML
    public void initialize()
    // special method that gets called after the UI has been built, do not have to call this
    // but gets called by Javafx
    {
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
    private void loadAccountsFromDB()
    {
        // loads accounts and puts all inside a list
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Platform, Username, Password FROM Accounts");

            while (rs.next())
            {
                String platform = rs.getString("Platform");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                accounts.add(new AccountInstance(password, platform, username));
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
                        }
                        // other instantiate the text with the correct
                        else {
                            setText(item.getPlatform() + " — " + item.getUsername());
                        }
                    }
                };
            });

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAccountPopup(AccountInstance account) {
        Dialog<Void> dialog = new Dialog<>();
        // void besause just a window and the "return" is really just it closing
        dialog.setTitle("Account Details");
        dialog.setHeaderText(account.getPlatform());

        DialogPane pane = dialog.getDialogPane();
        // this is what the actual window is
        pane.setContentText("Username: " + account.getUsername() + "\n" + "Platform: " + account.getPlatform() + "\n" + "Password: " + account.getPassword());
        // this is the content in the window
        pane.getButtonTypes().add(ButtonType.CLOSE);
        //this adds a close button to close the window

        dialog.showAndWait();
        //this actually opens the window and waits
    }

    @FXML
    private void onAddAccountClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/passmanager/AddAccountPopup.fxml"));
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



