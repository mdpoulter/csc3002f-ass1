/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 23 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ChatServerThread implements Runnable {
    private Socket chatSocket;
    private BufferedReader is = null;
    private Map<String, Socket> users;
    private String username;

    ChatServerThread(Socket chatSocket, Map<String, Socket> users, String username) {
        this.chatSocket = chatSocket;
        this.users = users;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            ChatProtocol.success(os);

            BufferedWriter os_ext;
            for (Map.Entry<String, Socket> entry : users.entrySet()) {
                String username_ext = entry.getKey();
                Socket chatSocket_ext = entry.getValue();

                if (!username_ext.equals(username)) {
                    os_ext = new BufferedWriter(new OutputStreamWriter(chatSocket_ext.getOutputStream()));
                    ChatProtocol.connect(os_ext, username);
                    ChatProtocol.connect(os, username_ext);
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

                            ChatProtocol.success(os);
                            break;
                        case "FILE":
                            os_ext = new BufferedWriter(new OutputStreamWriter(users.get(data.get("TO")).getOutputStream()));
                            ChatProtocol.messageFile(os_ext, data.get("FROM"), data.get("TO"), data.get("FILE"));

                            ChatProtocol.success(os);
                            break;
                        case "FILEBYTES":
                            os_ext = new BufferedWriter(new OutputStreamWriter(users.get(data.get("TO")).getOutputStream()));
                            ChatProtocol.sendFile(os_ext, null, data.get("FROM"), data.get("TO"), data.get("FILE"), null);

                            byte[] buffer = new byte[16 * 1024];
                            InputStream in = chatSocket.getInputStream();

                            int count;
                            while ((count = in.read(buffer)) > 0) {
                                users.get(data.get("TO")).getOutputStream().write(buffer, 0, count);
                            }

                            ChatProtocol.success(os);
                            break;
                        case "ACCEPTFILE":
                            os_ext = new BufferedWriter(new OutputStreamWriter(users.get(data.get("TO")).getOutputStream()));
                            ChatProtocol.acceptFile(os_ext, data.get("FROM"), data.get("TO"), data.get("FILE"));

                            ChatProtocol.success(os);
                            break;
                        case "DECLINEFILE":
                            os_ext = new BufferedWriter(new OutputStreamWriter(users.get(data.get("TO")).getOutputStream()));
                            ChatProtocol.declineFile(os_ext, data.get("FROM"), data.get("TO"), data.get("FILE"));

                            ChatProtocol.success(os);
                            break;
                        case "DISCONNECT":
                            for (Map.Entry<String, Socket> entry : users.entrySet()) {
                                String username_ext = entry.getKey();
                                Socket chatSocket_ext = entry.getValue();

                                if (!username_ext.equals(username)) {
                                    os_ext = new BufferedWriter(new OutputStreamWriter(chatSocket_ext.getOutputStream()));
                                    ChatProtocol.disconnect(os_ext, username);
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
            System.err.println("ExceptionA:  " + e);
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                System.err.println("ExceptionB:  " + e);
            }
        }
    }
}
