package com.resset.jrtclient;

import com.resset.jrtclient.net.IDGenerator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {


    public Button connectButton;
    public MenuButton selectShellMenu;
    public MenuItem bashMenuItem;
    public MenuItem shMenuItem;
    public MenuItem zshMenuItem;
    public TextField hostID;
    public TextField hostPassword;

    private String selectedShell = "/bin/bash";

    public void initialize() {
        bashMenuItem.setOnAction(event -> {
            selectShellMenu.setText(bashMenuItem.getText());
            selectedShell = "/bin/bash";
        });

        shMenuItem.setOnAction(event -> {
            selectShellMenu.setText(shMenuItem.getText());
            selectedShell = "cmd.exe";
        });
        zshMenuItem.setOnAction(event -> {
            selectShellMenu.setText(zshMenuItem.getText());
            selectedShell = "powershell.exe";
        });

        getHostID();
    }

    private void getHostID() {
        hostID.textProperty().setValue(IDGenerator.generateUUID().toString());
        hostPassword.textProperty().setValue("ABCD");
    }

    public void createSession() {
        try {
            createTerminalWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTerminalWindow () throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TerminalWindow.fxml"));
        Stage terminalStage = new Stage();
        loader.setController(new ShellController(
                terminalStage,
                selectedShell)
        );

        Parent terminalRoot = loader.load();

        Scene terminalScene = new Scene(terminalRoot);
        terminalStage.initModality(Modality.APPLICATION_MODAL);
        terminalStage.setScene(terminalScene);
        terminalStage.setTitle("Terminal");
//        terminalStage.setResizable(false);

        terminalStage.show();
    }
}