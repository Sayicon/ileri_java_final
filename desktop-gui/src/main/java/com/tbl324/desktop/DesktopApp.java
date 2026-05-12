package com.tbl324.desktop;

import javafx.application.Application;
import javafx.stage.Stage;

public class DesktopApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TBL324 Event Ticketing");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
