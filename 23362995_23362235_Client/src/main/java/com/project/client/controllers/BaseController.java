package com.project.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.project.client.Client;

import java.io.IOException;

public class BaseController {
    public static void switchScene(Stage stage, String fxmlFile, Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(BaseController.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();

            switch (fxmlFile) {
                case "add_lecture.fxml":
                    AddLectureController addLectureController = loader.getController();
                    addLectureController.setClient(client);
                    break;
                case "remove_lecture.fxml":
                    RemoveLectureController removeLectureController = loader.getController();
                    removeLectureController.setClient(client);
                    break;
                case "display_schedule.fxml":
                    DisplayScheduleController displayScheduleController = loader.getController();
                    displayScheduleController.initClient(client);
                    break;
                case "other.fxml":
                    OtherController otherController = loader.getController();
                    otherController.setClient(client);
                    break;
                case "front.fxml":
                    FrontController frontController = loader.getController();
                    frontController.setClient(client);
                    break;
            }

            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.show();
            
            BackgroundMusicController.getInstance().play();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}