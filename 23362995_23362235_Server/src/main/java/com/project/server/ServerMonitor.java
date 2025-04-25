package com.project.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controllers.ServerDisplayScheduleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerMonitor {

    private static final List<String> connectedClients = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> logMessages = Collections.synchronizedList(new ArrayList<>());

    @FXML
    private TextArea connectedClientsArea;  
    @FXML
    private TextArea logMessagesArea;  
    @FXML
    public Button backButton;
    @FXML
    private Button refreshButton;

    public static void addClient(String clientInfo) {
        connectedClients.add(clientInfo);
        log("Client connected: " + clientInfo);
    }

    public static void removeClient(String clientInfo) {
        connectedClients.remove(clientInfo);
        log("Client disconnected: " + clientInfo);
    }

    public static List<String> getConnectedClients() {
        return new ArrayList<>(connectedClients);
    }

    public static void log(String message) {
        String timestamp = "[" + java.time.LocalTime.now().withNano(0) + "] ";
        logMessages.add(timestamp + message);
        System.out.println(timestamp + message);
    }

    public static List<String> getLogs() {
        return new ArrayList<>(logMessages);
    }

    @FXML
    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/server_display_schedule.fxml"));
            BorderPane scheduleRoot = loader.load();

            ServerDisplayScheduleController scheduleController = loader.getController();
            scheduleController.setSchedule(Server.sharedSchedule);

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scheduleScene = new Scene(scheduleRoot, 1000, 700);
            stage.setScene(scheduleScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update the connected clients and log messages
    public void updateMonitor() {
        // Update connected clients area (TextArea)
        StringBuilder clientsText = new StringBuilder();
        for (String client : connectedClients) {
            clientsText.append(client).append("\n");
        }
        connectedClientsArea.setText(clientsText.toString());
    
        // Update log messages area
        StringBuilder logsText = new StringBuilder();
        for (String log : logMessages) {
            logsText.append(log).append("\n");
        }
        logMessagesArea.setText(logsText.toString());
    
        System.out.println("Updated monitor - Clients: " + connectedClients.size() + ", Logs: " + logMessages.size());
    }
    
    @FXML
    private void handleRefreshAction(){
        updateMonitor();
    }
}
