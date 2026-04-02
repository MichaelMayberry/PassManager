package com.example.passmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreenController
{
    @FXML
    private ListView<String> accountsList;

    @FXML
    private void onAddAccountClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/passmanager/AddAccountPopup.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Account");
            popupStage.setScene(new Scene(loader.load()));
            popupStage.showAndWait();


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}


