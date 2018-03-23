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

import java.io.FileInputStream;
import java.io.IOException;

public class ChatClient extends Application {
    private ChatClientController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        String fxmlDocPath = "src/ChatClientGUI.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
        VBox root = loader.load(fxmlStream);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();
        controller = loader.getController();
        controller.load();
    }

    @Override
    public void stop() {
        controller.disconnect();
    }
}
