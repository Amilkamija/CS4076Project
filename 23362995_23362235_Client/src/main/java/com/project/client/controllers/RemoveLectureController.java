package com.project.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.project.client.Client;

public class RemoveLectureController {
    private Client client;

    @FXML
    private ComboBox<String> dateComboBox;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private TextField moduleCodeField;

    @FXML
    private TextField roomCodeField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button submitButton;
    
    @FXML
    public void initialize() {
        //  09:00 - 17:00
        for (int hour = 9; hour <= 17; hour++) {
            this.timeComboBox.getItems().add(String.format("%02d:00", hour));
        }
    }

    public void setClient(Client client) {
        // Set the client instance if needed
        this.client = client;
    }

    @FXML
    private void handleCancel() {
        BaseController.switchScene((Stage) cancelButton.getScene().getWindow(), "front.fxml", client); //add lecture ---> front
    }

    @FXML
    private void handleSubmit() {
        String output = "";
        String moduleCode = this.moduleCodeField.getText().trim();
        String selectedDay = this.dateComboBox.getValue();
        String selectedTime = this.timeComboBox.getValue();
        String roomCode = this.roomCodeField.getText().trim();

        if (selectedDay == null || selectedTime == null || moduleCode.isEmpty() || roomCode.isEmpty()) {
            Client.showAlert("Invalid Input", "Please enter valid module code, day, time, and room number.");
            return;
        }

        // message should be of the form: "ACTION (e.g. ADD), module code, day (e.g. Monday), time (e.g. 14:00), lecture room"
        output = String.format("REMOVE,%s,%s,%s,%s", moduleCode, selectedDay, selectedTime, roomCode);
        System.out.println("\nClient: " + output);

        // create a background task
        String finalOutput = output;
        client.readResponse(finalOutput, () -> {
            try {
                // switch to schedule display if not already on it
                BaseController.switchScene((Stage) submitButton.getScene().getWindow(), "display_schedule.fxml", client);
            } catch (Exception e) {
                System.out.println("Scene Switch Error: " + e.getMessage());
                Client.showAlert("Scene Switch Error", e.getMessage());
                e.printStackTrace();
            }
        });
    }
}