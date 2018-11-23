package com.resset.jrtclient;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

import java.io.IOException;

public class ShellController {

    private final Stage parentStage;
    private final String[] commands;
    private Shell shell;
    public WebView htermWindow;

    public ShellController(Stage stage, String selectedShell) {
        this.parentStage = stage;

        if ((selectedShell.equals("powershell.exe")) || (selectedShell.equals("cmd.exe"))) {
            this.commands = new String[] {selectedShell, ""};
        } else {
            this.commands = new String[] {selectedShell, "-i"};
        }
    }

    public void initialize() {
        getWebEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> getWindow().setMember("app", this));
        getWebEngine().load(ShellController.class.getResource("/hterm/hterm.html").toExternalForm());


        parentStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            shell.disconnect();
            parentStage.close();
        });
    }


    @WebkitCall
    public void onTerminalReady() {
        this.shell = new LocalShell(getTerminalIO(),commands);
        try {
            shell.initProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @WebkitCall (from = "hterm")
    public void command(String command) {
        shell.command(command);
    }

    private JSObject getTerminalIO() {
        return (JSObject) getWebEngine().executeScript("t.io");
    }

    private WebEngine getWebEngine() {
        return htermWindow.getEngine();
    }

    public JSObject getWindow() {
        return (JSObject) getWebEngine().executeScript("window");
    }


}
