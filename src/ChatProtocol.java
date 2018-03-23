/*
 * Student No.: PLTMAT001, MDLKHA012, RTTCHA002
 * Assignment: 1
 * Course: CSC3002F
 * Date: 23 3 2018
 * Copyright (c) 2018. PLTMAT001, MDLKHA012, RTTCHA002
 */

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ChatProtocol {
    public static final int PORT = 12050;

    private static void open(BufferedWriter os) throws IOException {
        os.write("HELLO\n");
    }

    private static void close(BufferedWriter os) throws IOException {
        os.write("GOODBYE\n");
        os.flush();
    }

    public static void success(BufferedWriter os) throws IOException {
        open(os);
        os.write("SUCCESS: 0\n");
        close(os);
    }

    public static void failure(BufferedWriter os, String message) throws IOException {
        open(os);
        os.write("FAILURE: " + message + "\n");
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

    public static void messageFile(BufferedWriter os, String usernameFrom, String usernameTo, String filename) throws IOException {
        open(os);
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("FILE: " + filename + "\n");
        close(os);
    }

    public static void acceptFile(BufferedWriter os, String usernameFrom, String usernameTo, String filename) throws IOException {
        open(os);
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("ACCEPTFILE: " + filename + "\n");
        close(os);
    }

    public static void declineFile(BufferedWriter os, String usernameFrom, String usernameTo, String filename) throws IOException {
        open(os);
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("DECLINEFILE: " + filename + "\n");
        close(os);
    }

    public static void sendFile(BufferedWriter os, OutputStream dos, String usernameFrom, String usernameTo, String filename, File file, long length) throws IOException {
        length = (file != null || length == -1) ? file.length() : length;

        os.write("HELLOBYTES\n");
        os.write("FROM: " + usernameFrom + "\n");
        os.write("TO: " + usernameTo + "\n");
        os.write("FILEBYTES: " + filename + "\n");
        os.write("LENGTH: " + length + "\n");
        close(os);

        if (file != null) {
            byte[] buffer = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);

            int count;
            int counter = 0;
            while (counter < length) {
                count = in.read(buffer);
                counter += count;
                System.out.println("P:" + new String(buffer));
                dos.write(buffer, 0, count);
            }
            System.out.println("1");
            dos.flush();
            in.close();
        }
    }

    public static Map<String, String> receive(BufferedReader is) throws IOException {
        Map<String, String> m = new HashMap<>();

        String responseLine;
        while ((responseLine = is.readLine()) != null) {
            System.out.println(responseLine);
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
        } else if (m.containsKey("FILEBYTES")) {
            m.put("FUNCTION", "FILEBYTES");
        } else if (m.containsKey("SUCCESS")) {
            m.put("FUNCTION", "SUCCESS");
        } else if (m.containsKey("FAILURE")) {
            m.put("FUNCTION", "FAILURE");
        } else if (m.containsKey("ACCEPTFILE")) {
            m.put("FUNCTION", "ACCEPTFILE");
        } else if (m.containsKey("DECLINEFILE")) {
            m.put("FUNCTION", "DECLINEFILE");
        } else {
            m.put("FUNCTION", "UNKNOWN");
        }

        return m;
    }
}
