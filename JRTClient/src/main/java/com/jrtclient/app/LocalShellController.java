package com.jrtclient.app;

import com.jrtclient.annotation.WebkitCall;
import com.jrtclient.shell.LocalShell;
import com.jrtclient.shell.Shell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

import java.io.IOException;

public class LocalShellController {

    private final String[] commands;
    private final Clipboard clipboard;
    private final ClipboardContent clipboardContent;

    private Shell shell;
    public WebView htermWindow;

    public LocalShellController(String selectedShell) {
        this.clipboard = Clipboard.getSystemClipboard();
        this.clipboardContent = new ClipboardContent();
        if ((selectedShell.equals("powershell.exe")) || (selectedShell.equals("cmd.exe"))) {
            this.commands = new String[] {selectedShell, ""};
        } else {
            this.commands = new String[] {selectedShell, "-i"};
        }
    }

    public void initialize() {
        getWebEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> getWindow().setMember("app", this));
        getWebEngine().load(LocalShellController.class.getResource("/hterm/hterm.html").toExternalForm());
    }


    @WebkitCall
    public void onTerminalReady() {
        this.shell = new LocalShell(getTerminalIO(), commands);

        htermWindow.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            shell.disconnect();
        });

        try {
            shell.initProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @WebkitCall(from = "hterm")
    public void onShellResize(int columns, int rows) {
        shell.resizeShell(columns, rows);
    }

    @WebkitCall (from = "hterm")
    public void command(String command) {
        shell.command(command);
    }

    @WebkitCall(from = "hterm")
    public void copyClipboard(String clipboard) {
            clipboardContent.put(DataFormat.PLAIN_TEXT, clipboard);
            this.clipboard.setContent(clipboardContent);
//            System.out.printf("content: %s\n", clipboardContent.getString());
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
