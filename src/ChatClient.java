/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 23 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ChatClient extends Application {
    private ChatClientController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL location = getClass().getResource("ChatClientGUI.fxml");
        System.out.println(location.getPath());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);

        VBox root = fxmlLoader.load(location.openStream());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();
        controller = fxmlLoader.getController();
        controller.load();
    }

    @Override
    public void stop() {
        controller.disconnect();
    }
}
