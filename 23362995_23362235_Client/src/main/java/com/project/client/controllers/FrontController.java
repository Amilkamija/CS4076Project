package com.project.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.project.client.Client;

public class FrontController {
    @FXML
    private Button addLectureButton, removeLectureButton, displayScheduleButton, otherButton;
    
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleAddLecture() {
        BaseController.switchScene((Stage) addLectureButton.getScene().getWindow(), "add_lecture.fxml", client);
    }

    @FXML
    private void handleRemoveLecture() {
        BaseController.switchScene((Stage) removeLectureButton.getScene().getWindow(), "remove_lecture.fxml", client);
    }

    @FXML
    private void handleDisplaySchedule() {
        BaseController.switchScene((Stage) displayScheduleButton.getScene().getWindow(), "display_schedule.fxml", client);
    }

    @FXML
    private void handleOther() {
        BaseController.switchScene((Stage) otherButton.getScene().getWindow(), "other.fxml", client);
    }
}