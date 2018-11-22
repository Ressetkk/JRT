package com.resset.jrt;

import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TerminalWindow {
    public TextArea shellArea;
    private ShellController shellController;
    private Stage parentStage;
    private String[] commands;

    public TerminalWindow(Stage stage, String[] cmd) {
        this.parentStage = stage;
        this.commands = cmd;

    }

    public void initialize() throws IOException {
        shellArea.setFont(Font.font("Monospaced", 14));
        this.shellController = new ShellController(shellArea, commands);
        parentStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            shellController.disconnect();
            parentStage.close();
        });
    }
}
