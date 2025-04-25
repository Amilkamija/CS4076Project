package controllers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.project.server.Schedule;
import com.project.server.Server;
import com.project.server.ServerMonitor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ServerDisplayScheduleController implements com.project.server.ScheduleUpdateListener{

    public static final List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
    public static final List<String> times = List.of("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");

    private Schedule schedule;

    @FXML
    private GridPane scheduleGrid;

    @FXML
    private Label timetableLabel;

    @FXML
    private Button importButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button exportPDFButton;

    @FXML
    private Button exportCSVButton;

    @FXML
    private Button stopButton;

    @FXML
    private VBox connectedClientsBox;
    @FXML Label courseCodeLabel;

    @FXML
    private VBox logMessagesBox; 
    private final String[] timeSlots = {
            "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00",
            "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00"
    };

   //Called from ServerGUI to inject the shared schedule instacne
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        schedule.addListener(this);
        fetchAndDisplaySchedule();
    }

    @FXML
    public void initialize() {
        Label timeTitle = new Label("Time");
        timeTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        scheduleGrid.add(timeTitle, 0, 0);
        GridPane.setHalignment(timeTitle, HPos.CENTER); // Center it over the time labels

        // time column (first column)
        for (int i = 0; i < timeSlots.length; i++) {
            Label timeLabel = new Label(timeSlots[i]);
            timeLabel.setStyle("-fx-font-size: 14px;");
            scheduleGrid.add(timeLabel, 0, i + 1);
            GridPane.setHalignment(timeLabel, HPos.CENTER);
        }

        // days row (first row)
        for (int j = 1; j <= days.size(); j++) {
            Label dayLabel = new Label(days.get(j - 1));
            dayLabel.setStyle("-fx-font-size: 16px;");
            scheduleGrid.add(dayLabel, j, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
        }
    }

    public synchronized void fetchAndDisplaySchedule() {
        new Thread(() -> {
            System.out.println("\nServer: DISPLAY");
            String response = Server.sharedSchedule.getFormattedSchedule();

            updateScheduleGrid(response);
        }).start();
    }

    private synchronized void updateScheduleGrid(String response) {
        Platform.runLater(() -> {
            populateEmptySchedule();

            if (response == null || response.isEmpty() || response.equals("Schedule empty.")) {
                timetableLabel.setText("Schedule is empty.");
                return;
            }

            timetableLabel.setText("(Course Code) Schedule");

            String[] scheduleData = response.split(";");

            for (String schedule : scheduleData) {
                String[] details = schedule.split(",");

                if (details.length == 4) {
                    String module = details[0];
                    String day = details[1];
                    String time = details[2];
                    String room = details[3];

                    int row = times.indexOf(time) + 1;
                    int col = days.indexOf(day) + 1;

                    Platform.runLater(() -> {
                        // Add lecture to correct place in grid pane
                        if (row != 0 && col != 0) {
                            Label moduleLabel = new Label(module + "\n" + room);
                            scheduleGrid.add(moduleLabel, col, row);
                            GridPane.setHalignment(moduleLabel, HPos.CENTER);
                        } else {
                            System.out.println("Positioning error for schedule: position [" + row + "," + col + "]");
                        }
                    });
                }
            }
        });
    }

    private synchronized void populateEmptySchedule() {
        // Remove all the lecture slot labels, but keep the day names and times
        scheduleGrid.getChildren().removeIf(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Remove only the cells that are not part of the header (days/times)
            return colIndex != null && rowIndex != null && colIndex > 0 && rowIndex > 0;
        });

        for (int row = 1; row <= timeSlots.length; row++) {
            for (int col = 1; col <= 5; col++) {
                Label emptyCell = new Label("");
                emptyCell.setMinSize(150, 40); // Size logic for consistency
                emptyCell.setStyle("-fx-border-color: black; -fx-background-color: white;");
                scheduleGrid.add(emptyCell, col, row);
            }
        }
    }


    //GUI BUTTON HANDLERS 

    // Handle the stop action (closes the program)
    @FXML
    private void handleStopAction() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText(null);
            alert.setContentText("The program is closing.");
            alert.showAndWait();

            System.out.println("Program terminated.");
            Platform.exit();
            System.exit(0);
        });
    }

    // CLEAR SCHEDULE
    @FXML

    private void handleClear() {
        System.out.println("Client: CLEAR");
        String result = Server.sharedSchedule.clearSchedule();
        System.out.println("Server: " + result);
        ServerMonitor.log("Server has cleared the schedule.");
        showAlert("Success", "Schedule cleared.");
        fetchAndDisplaySchedule();
    }

    // import CSV
    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(scheduleGrid.getScene().getWindow());

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                StringBuilder formattedData = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    formattedData.append(line).append(";");
                }

                if (formattedData.length() > 0) {
                    formattedData.deleteCharAt(formattedData.length() - 1);
                }

                Server.sharedSchedule.clearSchedule();
                for (String entry : formattedData.toString().split(";")) {
                    String[] details = entry.split(",");
                    if (details.length == 4) {
                        Server.sharedSchedule.addLecture(new com.project.server.Lecture(
                                details[0], details[1], details[2], details[3]
                        ));
                    }
                }

                System.out.println("Server: Schedule imported successfully.");
                ServerMonitor.log("Server has imported a schedule from CSV.");

                showAlert("Success", "Schedule imported successfully.");

                fetchAndDisplaySchedule();

            } catch (IOException | com.project.server.IncorrectActionException e) {
                System.out.println("Server: Failed to import schedule.");
                showAlert("Error", "Failed to import schedule");
                e.printStackTrace();
            }
        }
    }


    //  exporting schedule to CSV
    @FXML
    private void handleExportCSV() {
        System.out.println("\nServer: Export as CSV");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(scheduleGrid.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                String response = Server.sharedSchedule.getFormattedSchedule();
                if (!response.isEmpty()) {
                    writer.write(response.replace(";", "\n"));
                }

                System.out.println("Server: Schedule exported successfully.");
                ServerMonitor.log("Server exported the schedule to CSV.");

                showAlert("Success", "Schedule exported successfully.");
            } catch (IOException e) {
                System.out.println("Server: Failed to export schedule.");
                showAlert("Error", "Failed to export schedule.");
                e.printStackTrace();
            }
        }
    }

    //  exporting schedule to PDF
    @FXML
    private void handleExportPDF() {
        System.out.println("\nServer: Exporting to PDF");
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("Error", "No printer job available.");
            return;
        }

        job.getJobSettings().setPageLayout(
            job.getPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM)
        );

        boolean proceed = job.showPrintDialog(scheduleGrid.getScene().getWindow());
        if (proceed) {
            boolean success = printNodeToPDF(scheduleGrid, job);
            if (success) {
                job.endJob();
                System.out.println("Server: Schedule exported successfully.");
                ServerMonitor.log("Server exported the schedule to PDF.");

                showAlert("Success", "Schedule exported successfully");
            } else {
                System.out.println("Server: Failed to export schedule.");
                showAlert("Error", "Failed to export schedule");
            }
        }
    }

    private boolean printNodeToPDF(Node node, PrinterJob job) {
        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        double padding = 40;
        double scaleX = pageLayout.getPrintableWidth() / (node.getBoundsInParent().getWidth() + padding);
        double scaleY = pageLayout.getPrintableHeight() / (node.getBoundsInParent().getHeight() + padding);
        double scale = Math.min(scaleX, scaleY);

        Scale scaleTransform = new Scale(scale, scale);
        node.getTransforms().add(scaleTransform);

        boolean success = job.printPage(node);

        node.getTransforms().remove(scaleTransform);

        return success;
    }
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert;
            if (title.equals("Success") || title.equals("Message")) {
                alert = new Alert(Alert.AlertType.INFORMATION);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
            }
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    @FXML
    private void handleMonitorAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/server_monitor.fxml"));
            BorderPane monitorRoot = loader.load();

            // Get the controller for ServerMonitor, if you want to set any data or call methods
            ServerMonitor monitorController = loader.getController();

            // Switch the scene to the monitor scene
            Stage stage = (Stage) stopButton.getScene().getWindow();
            Scene monitorScene = new Scene(monitorRoot, 1000, 700);
            stage.setScene(monitorScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  update the connected clients and logs
    public void updateServerMonitorDisplay() {
        
        connectedClientsBox.getChildren().clear();
        logMessagesBox.getChildren().clear();

        for (String client : ServerMonitor.getConnectedClients()) {
            connectedClientsBox.getChildren().add(new Label(client));
        }

        for (String log : ServerMonitor.getLogs()) {
            logMessagesBox.getChildren().add(new Label(log));
        }
    }
    @Override
public void onScheduleUpdated() {
    Platform.runLater(this::fetchAndDisplaySchedule);
}


}


