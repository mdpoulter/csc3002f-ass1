/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 16 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

public class ChatServerThread implements Runnable {
    private Socket chatSocket = null;
    private BufferedReader is = null;
    private BufferedWriter os = null;
    private BufferedWriter os_ext = null;
    private Map<String, Socket> users = null;
    private String username;

    public ChatServerThread(Socket chatSocket, Map<String, Socket> users, String username) {
        this.chatSocket = chatSocket;
        this.users = users;
        this.username = username;
    }

    public void run() {
        try {
            os = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            ChatProtocol.success(os);

            for (Map.Entry<String, Socket> entry : users.entrySet()) {
                String username_ext = entry.getKey();
                Socket chatSocket_ext = entry.getValue();

                if (!username_ext.equals(username)) {
                    os_ext = new BufferedWriter(new OutputStreamWriter(chatSocket_ext.getOutputStream()));
                    ChatProtocol.connect(os_ext, username);
                    os_ext.close();
                }
            }

            String responseLine;
            Map<String, String> data;
            boolean open = true;
            while (open) {
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
                            os_ext = new BufferedWriter(new OutputStreamWriter(users.get(data.get("TO")).getOutputStream()));
                            ChatProtocol.messageText(os_ext, data.get("FROM"), data.get("TO"), data.get("TEXTMESSAGE"));
                            os_ext.close();

                            ChatProtocol.success(os);
                            break;
                        case "MESSAGEFILE":
                            //TODO: Write messages
                            ChatProtocol.failure(os, "Unsupported.");
                            break;
                        case "DISCONNECT":
                            for (Map.Entry<String, Socket> entry : users.entrySet()) {
                                String username_ext = entry.getKey();
                                Socket chatSocket_ext = entry.getValue();

                                if (!username_ext.equals(username)) {
                                    os_ext = new BufferedWriter(new OutputStreamWriter(chatSocket_ext.getOutputStream()));
                                    ChatProtocol.disconnect(os_ext, username);
                                    os_ext.close();
                                }
                            }

                            open = false;

                            ChatProtocol.success(os);
                            break;
                    }
                }
            }

            users.remove(username);

        } catch (Exception e) {
            System.err.println("Exception:  " + e);
        } finally {
            try {
                os.close();
                is.close();
            } catch (Exception e) {
                System.err.println("Exception:  " + e);
            }
        }
    }
}
