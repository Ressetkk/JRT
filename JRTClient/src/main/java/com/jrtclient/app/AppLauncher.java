package com.jrtclient.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.stage.WindowEvent;


public class AppLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Parent mainRoot = FXMLLoader.load(getClass().getResource("/views/MainWindow.fxml"));
        Scene mainScene = new Scene(mainRoot);
        mainScene.getStylesheets().add(getClass().getResource("/css/bootstrap3.css").toExternalForm());
        mainStage.setScene(mainScene);
        mainStage.setTitle("JRT");
        mainStage.setResizable(false);
        mainScene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            System.exit(0);
        });
        mainStage.show();
    }
}
