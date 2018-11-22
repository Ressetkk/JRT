package com.resset.jrt;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

import java.io.IOException;

public class WebTerminalWindow {

    private final Stage parentStage;
    private final String[] commands;
    private ShellController shellController;
    public WebView htermWindow;

    public WebTerminalWindow(Stage stage, String[] cmd) {
        this.parentStage = stage;
        this.commands = cmd;
    }

    public void initialize() throws IOException {
        this.shellController = new ShellController(htermWindow,commands);


        parentStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {

            parentStage.close();
        });
    }
}
