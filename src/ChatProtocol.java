/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 16 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatProtocol {
    private static void open(BufferedWriter os) throws IOException {
        os.write("HELLO\n");
    }

    private static void close(BufferedWriter os) throws IOException {
        os.write("GOODBYE\n");
        os.flush();
    }

    private static void success(BufferedWriter os) throws IOException {
        open(os);
        os.write("SUCCESS: 0\n");
        close(os);
    }

    private static void failure(BufferedWriter os) throws IOException {
        open(os);
        os.write("FAILURE: 0\n");
        close(os);
    }

    public static void connect(BufferedWriter os, String username) throws IOException {
        open(os);
        os.write("CONNECT: " + username + "\n");
        close(os);
    }

    public static void disconnect(BufferedWriter os, String username) throws IOException {
        open(os);
        os.write("DISCONNECT: " + username + "\n");
        close(os);
    }

    public static void messageText(BufferedWriter os, String usernameFrom, String usernameTo, String message) throws IOException {
        open(os);
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("TEXTMESSAGE: " + message + "\n");
        close(os);
    }

    public static void messageFile(BufferedWriter os, String usernameFrom, String usernameTo, String file) throws IOException {
        open(os);
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("FILE: " + file + "\n");
        close(os);
    }

    public static Map<String, String> receive(BufferedReader is) throws IOException {
        Map<String, String> m = new HashMap<>();

        String responseLine;
        while ((responseLine = is.readLine()) != null) {
            if (responseLine.contains("GOODBYE")) {
                break;
            }
            m.put(responseLine.split(": ")[0], responseLine.split(": ")[1]);
        }

        if (m.containsKey("CONNECT")) {
            m.put("FUNCTION", "CONNECT");
        } else if (m.containsKey("DISCONNECT")) {
            m.put("FUNCTION", "DISCONNECT");
        } else if (m.containsKey("TEXTMESSAGE")) {
            m.put("FUNCTION", "TEXTMESSAGE");
        } else if (m.containsKey("FILE")) {
            m.put("FUNCTION", "FILE");
        } else if (m.containsKey("SUCCESS")) {
            m.put("FUNCTION", "SUCCESS");
        } else if (m.containsKey("FAILURE")) {
            m.put("FUNCTION", "FAILURE");
        } else {
            m.put("FUNCTION", "UNKNOWN");
        }

        return m;
    }
}
