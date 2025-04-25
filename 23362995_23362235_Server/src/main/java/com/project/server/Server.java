package com.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static ServerSocket serverSocket;
    private static ServerSocket updateSocket;
    protected static int clientsConnected;
    protected static int offset;

    // 👇 Shared schedule for GUI + clients
    public static Schedule sharedSchedule = new Schedule();

    protected static ConcurrentHashMap<String, Schedule> database = new ConcurrentHashMap<>();

    public static void startServer(int port) {
        System.out.println("Opening port...\n");

        try {
            serverSocket = new ServerSocket(port);
            updateSocket = new ServerSocket(port + 1);
            System.out.println("Server connected to port " + port);
            System.out.println("Update Socket connected to port " + (port + 1));
        } catch (IOException ex) {
            System.out.println("Port connection error at ports " + port + " and " + (port + 1));
            return;
        }

        while (true) {
            try {
                Socket link = serverSocket.accept();
                Socket updateLink = updateSocket.accept();
                clientsConnected++;
                System.out.println("\nNew client connected. Total clients: " + clientsConnected);

                BufferedReader tempIn = new BufferedReader(new InputStreamReader(link.getInputStream()));
                String message = tempIn.readLine().trim();

                String course = "";
                if (message.contains("courseCode")) {
                    course = message.split(":")[1].trim();
                }

                Schedule schedule = sharedSchedule; 
                new Thread(new ClientHandler(link, updateLink, schedule, clientsConnected + offset, course)).start();
            } catch (IOException ex) {
                System.out.println("Failed to connect to client.");
            }
        }
    }

    public static void main(String[] args) {
        startServer(1234);
    }
}

