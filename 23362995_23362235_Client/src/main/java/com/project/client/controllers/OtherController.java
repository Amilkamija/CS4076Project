package com.project.client.controllers;

import java.io.IOException;

import com.project.client.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OtherController {
    private Client client;

    @FXML
    private Button backButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button otherButton;

    @FXML
    private Button earlyLecturesButton;

    public void setClient(Client client) {
        // Set the client instance if needed
        this.client = client;
    }

    @FXML
    private void handleStop() {
        String message = "STOP";
        client.readResponse(message, () -> {
            // stop the music
            BackgroundMusicController.getInstance().stop();
            // then close the program successfully
            System.exit(0);
        });
    }

    @FXML
    private void handleOther(){
        String message = "OTHER";
        client.readResponse(message, null);
    }

    @FXML
    private void handleBack() {
        BaseController.switchScene((Stage) backButton.getScene().getWindow(), "front.fxml", client); //other ---> front
    }

    @FXML
    private void handleEarlyLectures() {
        String message = "EARLY";
        client.readResponse(message, () -> {
            try {
                // switch to schedule display if not already on it
                BaseController.switchScene((Stage) earlyLecturesButton.getScene().getWindow(), "display_schedule.fxml", client);
            } catch (Exception e) {
                System.out.println("Scene Switch Error: " + e.getMessage());
                Client.showAlert("Scene Switch Error", e.getMessage());
                e.printStackTrace();
            }});
    }
    
    @FXML
    private void handleShowServerSchedule() {
        try {
            String javafxPath = "C:\\Program Files\\javafx-sdk-23.0.2\\lib";
            String command = String.format(
                "java --module-path \"%s\" --add-modules javafx.controls,javafx.fxml -jar libs/server_gui.jar",
                javafxPath
            );
            Process process = Runtime.getRuntime().exec(command);
            System.out.println("Server GUI launched with JavaFX modules.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

