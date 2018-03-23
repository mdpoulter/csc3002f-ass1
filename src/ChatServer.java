/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 23 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static ServerSocket serverSocket = null;
    private static Socket chatSocket = null;
    private static BufferedWriter os = null;
    private static BufferedReader is = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(ChatProtocol.PORT);
            Map<String, Socket> users = new HashMap<>();

            String responseLine;
            Map<String, String> data;
            System.out.println("Server started");
            while (true) {
                System.out.println("Waiting for connections...");
                chatSocket = serverSocket.accept();

                os = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
                is = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

                data = null;
                while ((responseLine = is.readLine()) != null) {
                    if (responseLine.contains("HELLO")) {
                        data = ChatProtocol.receive(is);
                        break;
                    }
                }

                if (data != null && data.get("FUNCTION").equals("CONNECT") && !users.containsKey(data.get("CONNECT"))) {
                    users.put(data.get("CONNECT"), chatSocket);
                    ChatServerThread cst = new ChatServerThread(chatSocket, users, data.get("CONNECT"));
                    Thread t = new Thread(cst);
                    t.start();
                    System.out.println("User connected: " + data.get("CONNECT"));
                } else {
                    ChatProtocol.failure(os, "Username already exists.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
                chatSocket.close();
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
