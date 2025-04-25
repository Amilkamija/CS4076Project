package com.project.server;

import controllers.ServerDisplayScheduleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private static Stage primaryStage;

    public static void launchGUI() {
        new Thread(() -> Application.launch(ServerGUI.class)).start();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("Server GUI");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/server_display_schedule.fxml"));
            BorderPane root = loader.load();

            ServerDisplayScheduleController controller = loader.getController();
            controller.setSchedule(Server.sharedSchedule);

            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.show();

            
            new Thread(() -> Server.startServer(1234)).start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

