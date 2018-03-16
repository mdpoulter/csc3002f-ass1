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

    public ChatServerThread(Socket chatSocket, Map<String, Socket> users) {
        this.chatSocket = chatSocket;
        this.users = users;
    }

    public void run() {
        try {
            os = new BufferedWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            ChatProtocol.success(os);

            String responseLine;
            Map<String, String> data;
            while (true) {
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
                    }
                }
            }

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
