package com.kryvapust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSMTP {
    BufferedReader reader; // объекта для чтения потока символов входящих сообщений от клиента
    BufferedWriter writer; // объект для отправки потока символов сообщения клиенту

    public void start() {
        int port = 25; // порт smtp сервера
        try {
            ServerSocket serverSocket = new ServerSocket(port); // создание локального сервера на порту 25
            System.out.printf("SMTP Server started on %d port%n", port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // создание сокета клиента
                // инициализация на основании сокета клиента объекта для чтения потока символов от клиента
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // инициализация на основании сокета клиента объекта для отправки потока символов клиенту
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                // приветственное сообщение сервера об успешном подключении (код 220)
                sendMessage("220 localhost SMTP LOCAL");
                // чтение сообщения от сервера [EHLO - команда приветствие от клиента]
                readMessage();
                // ответ сервера на команду EHLO - код 250
                sendMessage("250 smtp.localhost is ready");
                // чтение сообщения от сервера [MAIL FROM - команда для указания отправителя сообщения]
                readMessage();
                // ответ сервера на команду MAIL FROM - код 250
                sendMessage("250 OK");
                // чтение сообщения от сервера [RCPT TO - команда для указания получателя сообщения]
                readMessage();
                // ответ сервера на команду RCPT TO - код 250
                sendMessage("250 OK");
                // чтение сообщения от сервера [DATA - команда для указания тела письма]
                readMessage();
                // ответ сервера на команду DATA - код 354 (приглашение вводить письмо) и указания строки, которой нужно закончить письмо
                sendMessage("354 Start mail input; end with <CRLF>.<CRLF>");
                String data = ""; // переменная, в которую записывается каждая строка сообщения от клиента
                // чтение сообщений от клинета пока не придет строка-окончание письма (точка)
                while (!".".equals(data)) {
                    data = readMessage();
                }
                sendMessage("250 Ok"); // ответ сервера на команду DATA - письмо получено, код 250
                readMessage();  // чтение сообщения от сервера [QUIT - завершение соединения]
                sendMessage("221 smtp.localhost is closing transmission channel"); // ответ сервера на команду QUIT - код 221

                //закрытие потоков чтения и записи для клиента
                reader.close();
                writer.close();
                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // метод для чтения сообщений от клиента и печати этих сообщений в консоли
    private String readMessage() {
        try {
            String line = reader.readLine();
            System.out.printf("Client: %s \n", line);
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // метод для отправки сообщений клиенту и печати этих сообщений в консоли
    void sendMessage(String text) {
        try {
            writer.write(text + "\n");
            writer.flush();
            System.out.printf("Server: %s \n", text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
