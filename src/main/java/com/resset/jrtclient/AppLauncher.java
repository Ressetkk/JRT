package com.resset.jrtclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


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
        mainStage.show();
    }
}
