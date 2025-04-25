module com.project.server {
    requires javafx.controls;
    requires javafx.fxml;
   


    // Open both the controllers and com.project.server packages to javafx.fxml
    opens controllers to javafx.fxml;
    opens com.project.server to javafx.fxml;  // Add this line

    exports com.project.server;
}


