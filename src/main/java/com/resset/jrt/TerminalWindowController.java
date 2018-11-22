package com.resset.jrt;

import com.sun.javafx.PlatformUtil;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TerminalWindowController {

    private final Stage parentStage;
    private final String[] commands;
    private Shell shell;
    public WebView htermWindow;

    public TerminalWindowController(Stage stage, String selectedShell) {
        this.parentStage = stage;

        if ((selectedShell.equals("powershell.exe")) || (selectedShell.equals("cmd.exe"))) {
            this.commands = new String[] {selectedShell, ""};
        } else {
            this.commands = new String[] {selectedShell, "-i"};
        }
    }

    public void initialize() {
        this.shell = new LocalShell(htermWindow, commands);
        parentStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            shell.disconnect();
            parentStage.close();
        });
    }
}
