package com.kryvapust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSMTP {
    BufferedReader reader;
    BufferedWriter writer;

    public void start() {
        int port = 8082;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.printf("SMTP Server started on %d port%n", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                send220();
                readMessage(); // EHLO [IPv6:::1]
                send250commandList();
                readMessage(); // MAIL FROM:<bgd.sao@gmail.com>
                send250_2_1_0_Ok();
                readMessage(); // RCPT TO:<anzhelikaShch11@gmail.com>
                send250_2_1_5_Ok();
                readMessage(); // DATA
                send354();
                String s = "";
                while (!".".equals(s)) {
                    s = readMessage();
                }
                send250Ok();
                readMessage(); // QUIT
                send221();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send220() {
        sendMessage("220 localhost SMTP OWN_SERVER");
    }

    private void send221() {
        sendMessage("221 2.0.0 Service closing transmission channel");
    }

    private void send250Ok() {
        sendMessage("250 Ok");
    }

    private void send354() {
        sendMessage("354 Start mail input; end with <CRLF>.<CRLF>");
    }

    private void send250_2_1_5_Ok() {
        sendMessage("250 2.1.5 Ok");
    }

    private void send250_2_1_0_Ok() {
        sendMessage("250 2.1.0 Ok");
    }

    private void send250commandList() {
        sendMessage("250 smtp.local is ready");
    }

    private String readMessage() {
        try {
            String line = reader.readLine();
            System.out.println(line);
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void sendMessage(String text) {
        try {
            writer.write(text + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
