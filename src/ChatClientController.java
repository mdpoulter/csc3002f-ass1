/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 16 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class ChatClientController {

    private static String HOST = "127.0.0.1";
    private static int PORT = 6789;
    private Socket chatSocket = null;
    private BufferedWriter os = null;
    private BufferedReader is = null;
    private boolean connected = false;

    @FXML
    private ListView<?> lsvUsers;

    @FXML
    private TextArea txaMessages;

    @FXML
    private Button btnConnect;

    @FXML
    private TextField txfMessage;

    @FXML
    private TextField txfServer;

    @FXML
    private Button btnSendMessage;

    @FXML
    private TextField txfUsername;

    @FXML
    void connect(ActionEvent event) {
        if (!txfServer.getText().equals("")) {
            HOST = txfServer.getText();
        }
        try {
            chatSocket = new Socket(HOST, PORT);
            os = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Can't find server...");
        } catch (IOException e) {
            System.err.println("Couldn't make i/o connection to server...");
        }

        if (chatSocket != null && os != null && is != null) {
            try {
                ChatProtocol.connect(os, txfUsername.getText());

                String responseLine;
                Map<String, String> data = null;
                while ((responseLine = is.readLine()) != null) {
                    if (responseLine.contains("HELLO")) {
                        data = ChatProtocol.receive(is);
                        break;
                    }
                }

                if (data != null && data.get("FUNCTION").equals("SUCCESS")) {
                    connected = true;
                    waitForMessages();
                } else if (data != null && data.get("FUNCTION").equals("FAILURE")) {
                    messageBox(data.get("FAILURE"));
                } else {
                    System.err.println("Some other error");
                }

                os.close();
                is.close();
                chatSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {

    }

    private void messageBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void waitForMessages() throws IOException {
        String responseLine;
        Map<String, String> data;
        while (connected) {
            data = null;
            while ((responseLine = is.readLine()) != null) {
                if (responseLine.contains("HELLO")) {
                    data = ChatProtocol.receive(is);
                    break;
                }
            }

            if (data != null) {
                switch (data.get("FUNCTION")) {
                    case "TEXTMESSAGE":

                        break;
                    case "MESSAGEFILE":
                        //TODO: Write messages
                        break;
                    case "CONNECT":

                        break;
                    case "DISCONNECT":

                        break;
                }
            }
        }
    }
}
