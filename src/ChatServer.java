/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 16 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static int PORT = 6789;
    private static ServerSocket serverSocket = null;
    private static Socket chatSocket = null;
    private static BufferedWriter os = null;
    private static BufferedReader is = null;
    private static Map<String, Socket> users = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            users = new HashMap<>();

            String responseLine;
            Map<String, String> data;
            while (true) {
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
                    ChatServerThread cst = new ChatServerThread(chatSocket, users);
                    Thread t = new Thread(cst);
                    t.start();
                } else {
                    ChatProtocol.failure(os, "Username already exists.");
                }
            }
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        } finally {
            try {
                os.close();
                is.close();
                chatSocket.close();
                serverSocket.close();
            } catch (Exception e) {
                System.err.println("Exception:  " + e);
            }
        }
    }
}
