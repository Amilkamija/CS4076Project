package com.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private final Socket link;
    private final Socket updateLink;
    private final Schedule schedule;
    private final int clientNumber;
    private String courseCode;
    private BufferedReader in;
    private PrintWriter out;
    private PrintWriter updateOut;

    public ClientHandler(Socket socket, Socket updateSocket, Schedule schedule, int clientNumber, String courseCode) {
        this.link = socket;
        this.updateLink = updateSocket;
        this.schedule = schedule;
        this.clientNumber = clientNumber;
        this.courseCode = courseCode;

//Registers the client in schedule and monitor
        schedule.addClient(this);
        ServerMonitor.addClient(this.toString()); 
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader((new InputStreamReader((link.getInputStream())))); //set up message receiver
            out = new PrintWriter(link.getOutputStream(), true); //set up message writer
            updateOut = new PrintWriter(updateLink.getOutputStream(), true); //set up update writer

            String message = "";
            while((message = in.readLine()) != null){
                message = message.trim();
                if(message.isEmpty())continue;
                System.out.println("\nClient " + this.toString() + ": " + message);

                String response = processRequest(message);
                out.println(response);
                System.out.println("Server: " + response);

                if(message.equalsIgnoreCase("STOP")){
                    break;
                }
            }

            //Disconnects the client
            System.out.printf("\nClient %s disconed. Total clients: %d\nnect\n", this.toString(), (Server.clientsConnected - 1));
            ServerMonitor.removeClient(this.toString()); // Track disconnection
            link.close();
            synchronized (this) {
                Server.clientsConnected--;
                Server.offset++;
            }
            schedule.removeClient(this);
        } catch (IOException ex) {
            System.out.println("Connection error: " + ex.getMessage());
        }
    }


    private String processRequest(String message){
        String[] str = message.split(",", 2);
        String action = str[0].toUpperCase();
        boolean mod = false;

        try {
            switch (action) {
                case "ADD":
                    String[] addParts = str[1].split(",");
                    Lecture addLec = new Lecture(addParts[0], addParts[1], addParts[2], addParts[3]);
                    mod = true;
                    ServerMonitor.log(this.toString() + " added a lecture: " + str[1]);
                    return schedule.addLecture(addLec);
                case "REMOVE":
                    String[] remParts = str[1].split(",");
                    Lecture remLec = new Lecture(remParts[0], remParts[1], remParts[2], remParts[3]);
                    mod = true;
                    ServerMonitor.log(this.toString() + " removed a lecture: " + str[1]);
                    return schedule.removeLecture(remLec);
                case "DISPLAY":
                ServerMonitor.log(this.toString() + " viewed the schedule.");
                    return schedule.displaySchedule();
                case "IMPORT":
                    String[] importedEntries = str[1].split(";");
                    for (String entry : importedEntries) {
                        //split each module into parts and add them to the schedule if possible
                        String[] parts = entry.split(",");
                        Lecture lec = new Lecture(parts[0], parts[1], parts[2], parts[3]);
                        schedule.addLecture(lec);
                    }
                    mod = true;
                     ServerMonitor.log(this.toString() + " imported a schedule.");
                    return "Success: Schedule imported successfully.";
                case "CLEAR":
                    mod = true;
                    ServerMonitor.log(this.toString() + " cleared the schedule.");
                    return schedule.clearSchedule();
                case "EARLY":
                    mod = true;
                    ServerMonitor.log(this.toString() + " requested early lectures adjustment.");
                    return schedule.earlyLectures();
                case "STOP":
                    return "TERMINATE: Closing connection...";
                default:
                    throw new IncorrectActionException("Apologies, Amelia and Emily don't provide this service.");
            }
        } catch (IncorrectActionException ex) {
            return "Incorrect Action: " + ex.getMessage();
        } 
        finally {
            if(mod) {
                broadcast("UPDATE");
            }
        }
    }

    //Sends message to all connected clients
    public void broadcast(String message) {
        for (ClientHandler client : schedule.getClients()) {
            if (client != null && client.updateOut != null && client != this) { // don't send to self
                client.updateOut.println(message);
                client.updateOut.flush();
            }
        }
    }

    @Override
    public String toString() {
        return clientNumber + " (" + courseCode + ")";
    }
}