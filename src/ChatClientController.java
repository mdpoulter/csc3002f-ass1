/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 23 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChatClientController {

    private static String HOST = "localhost";
    private Socket chatSocket = null;
    private BufferedWriter os = null;
    private BufferedReader is = null;
    private final FileChooser fileChooser = new FileChooser();
    private OutputStream dos = null;
    private boolean connected = false;
    private String username;
    private InputStream dis = null;
    private Map<String, String> messages = new HashMap<>();
    private Map<String, File> files = new HashMap<>();
    @FXML
    private Pane panConnect;

    @FXML
    private Pane panMessages;

    @FXML
    private ListView<String> lsvUsers;

    @FXML
    private TextArea txaMessages;

    @FXML
    private Pane panUsers;

    @FXML
    private TextField txfMessage;

    @FXML
    private TextField txfServer;

    @FXML
    private Button btnSendFile;

    @FXML
    private TextField txfUsername;

    @FXML
    void connect(ActionEvent event) {
        if (txfUsername.getText().equals("")) {
            messageBox("Please enter a username.");
        } else {
            panConnect.setDisable(true);
            if (!txfServer.getText().equals("")) {
                HOST = txfServer.getText();
            }
            try {
                chatSocket = new Socket(HOST, ChatProtocol.PORT);
                dos = chatSocket.getOutputStream();
                os = new BufferedWriter(new OutputStreamWriter(dos));
                dis = chatSocket.getInputStream();
                is = new BufferedReader(new InputStreamReader(dis));
            } catch (UnknownHostException e) {
                System.err.println("Can't find server...");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Couldn't make i/o connection to server...");
            }

            if (chatSocket != null && os != null && is != null) {
                try {
                    username = txfUsername.getText();
                    ChatProtocol.connect(os, username);

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

                        MessageWaiting mw = new MessageWaiting();
                        Thread t = new Thread(mw);
                        t.start();

                        panMessages.setDisable(false);
                        panUsers.setDisable(false);
                    } else if (data != null && data.get("FUNCTION").equals("FAILURE")) {
                        messageBox(data.get("FAILURE"));
                        panConnect.setDisable(false);
                    } else {
                        System.err.println("Some other error");
                    }
                } catch (IOException e) {
                    System.err.println("IOExceptionH:  " + e);
                }
            } else {
                panConnect.setDisable(false);
            }
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = txfMessage.getText();
        String usernameTo = lsvUsers.getSelectionModel().getSelectedItem();
        if (!message.equals("")) {
            try {
                ChatProtocol.messageText(os, username, usernameTo, message);
                addMessage(usernameTo, username, message);
                txfMessage.setText("");
            } catch (IOException e) {
                System.err.println("IOExceptionG:  " + e);
            }
        }
    }

    @FXML
    void sendFile(ActionEvent event) {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        String usernameTo = lsvUsers.getSelectionModel().getSelectedItem();
        if (file != null) {
            try {
                ChatProtocol.messageFile(os, username, usernameTo, file.getName());
                files.put(file.getName(), file);
                addMessage(usernameTo, username, "File sent: " + file.getName());
            } catch (IOException e) {
                System.err.println("IOExceptionI:  " + e);
            }
        }
    }

    private void addMessage(String username, String usernameFrom, String message) {
        messages.put(username, (messages.get(username) == null ? "" : messages.get(username)) + usernameFrom + ": " + message + "\n");
        reprintMessages();
    }

    private void reprintMessages() {
        if (lsvUsers.getSelectionModel().getSelectedItem() == null) {
            txaMessages.setText("");
        } else {
            txaMessages.setText(messages.get(lsvUsers.getSelectionModel().getSelectedItem()));
        }
        txaMessages.positionCaret(txaMessages.getText().length());
    }

    void load() {
        lsvUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            reprintMessages();
            txfMessage.setText("");
            txfMessage.requestFocus();
        });
    }

    void disconnect() {
        try {
            if (os != null) {
                ChatProtocol.disconnect(os, username);
            }
            connected = false;
        } catch (IOException e) {
            System.err.println("IOExceptionG:  " + e);
        }
    }

    private void messageBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private boolean fileMessageBox(String username, String filename) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Receive file?");
        alert.setHeaderText(null);
        alert.setContentText("You have been sent a file (" + filename + ") by " + username + ". Do you wish to accept?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Select file to send");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private class MessageWaiting implements Runnable {
        @Override
        public void run() {
            String responseLine;
            Map<String, String> data;

            try {
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
                                Map<String, String> finalData = data;
                                Platform.runLater(() -> addMessage(finalData.get("FROM"), finalData.get("FROM"), finalData.get("TEXTMESSAGE")));
                                break;
                            case "MESSAGEFILE":
                                Map<String, String> finalData3 = data;
                                Platform.runLater(() -> {
                                    if (fileMessageBox(finalData3.get("FROM"), finalData3.get("FILE"))) {
                                        try {
                                            ChatProtocol.acceptFile(os, username, finalData3.get("FROM"), finalData3.get("FILE"));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            ChatProtocol.declineFile(os, username, finalData3.get("FROM"), finalData3.get("FILE"));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        addMessage(finalData3.get("FROM"), finalData3.get("FROM"), "File declined: " + finalData3.get("FILE"));
                                    }

                                });
                                break;
                            case "ACCEPTFILE":
                                Map<String, String> finalData5 = data;

                                ChatProtocol.sendFile(os, dos, username, finalData5.get("FROM"), finalData5.get("FILE"), files.get(finalData5.get("FILE")));

                                files.remove(finalData5.get("FILE"));
                                Platform.runLater(() -> addMessage(finalData5.get("FROM"), finalData5.get("FROM"), "File accepted: " + finalData5.get("FILE")));
                                break;
                            case "DECLINEFILE":
                                Map<String, String> finalData4 = data;
                                files.remove(finalData4.get("FILE"));
                                Platform.runLater(() -> addMessage(finalData4.get("FROM"), finalData4.get("FROM"), "File declined: " + finalData4.get("FILE")));
                                break;
                            case "FILEBYTES":
                                Map<String, String> finalData6 = data;
                                Platform.runLater(() -> {
                                    try {
                                        DirectoryChooser chooser = new DirectoryChooser();
                                        chooser.setTitle("Select folder to save file");
                                        chooser.setInitialDirectory(
                                                new File(System.getProperty("user.home"))
                                        );
                                        File path = chooser.showDialog(btnSendFile.getScene().getWindow());

                                        byte[] buffer = new byte[16 * 1024];
                                        File file = new File(path, finalData6.get("FILE"));

                                        boolean result = file.createNewFile();

                                        OutputStream out = new FileOutputStream(file, false);

                                        int count;
                                        while ((count = dis.read(buffer)) > 0) {
                                            out.write(buffer, 0, count);
                                        }

                                        out.flush();
                                        out.close();

                                        addMessage(finalData6.get("FROM"), finalData6.get("FROM"), "File received: " + finalData6.get("FILE"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                break;
                            case "CONNECT":
                                Map<String, String> finalData1 = data;
                                Platform.runLater(() -> lsvUsers.getItems().add(finalData1.get("CONNECT")));
                                break;
                            case "DISCONNECT":
                                Map<String, String> finalData2 = data;
                                Platform.runLater(() -> {
                                    lsvUsers.getItems().remove(finalData2.get("DISCONNECT"));
                                    messages.remove(finalData2.get("DISCONNECT"));
                                });
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("IOExceptionE: " + e);
            } finally {
                try {
                    os.close();
                    is.close();
                    chatSocket.close();
                } catch (IOException e) {
                    System.err.println("IOExceptionF: " + e);
                }
            }


        }
    }
}

