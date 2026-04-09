package com.example.passmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * JavaFX class that extends Application in order to be the entry point for the UI
 * and allows the creation of stages and scenes on top of it
 */
public class HelloApplication extends Application {

    /**
     * Called at runtime and loads the FXML file to be the first screen seen to which we can then interact with.
     * Also sets the header of the windows name. Instantiates JavaFX
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws IOException if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("first-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PassManager");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
// just used to instantiate javafx