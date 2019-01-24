package com.jrtclient.app;

import com.jrtclient.annotation.WebkitCall;
import com.jrtclient.net.TerminalIncomingMessagesHandler;
import com.jrtclient.shell.LocalShell;
import com.jrtclient.shell.Shell;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

import java.io.IOException;

public class RemoteShellController {

    private final String[] commands;
    private final Clipboard clipboard;
    private final ClipboardContent clipboardContent;
    private final Channel channel;

    public WebView htermWindow;

    public RemoteShellController(String[] commands, Channel channel) {
        this.channel = channel;
        this.clipboard = Clipboard.getSystemClipboard();
        this.clipboardContent = new ClipboardContent();
        this.commands = commands;
    }

    public void initialize() {
        getWebEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> getWindow().setMember("app", this));
        getWebEngine().load(RemoteShellController.class.getResource("/hterm/hterm.html").toExternalForm());
    }


    @WebkitCall
    public void onTerminalReady() {

        htermWindow.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            channel.writeAndFlush("disconnect");
            channel.pipeline().remove("TerminalIncomingMessagesHandler");
        });

        channel.pipeline().addBefore("mainLogic", "TerminalIncomingMessagesHandler", new TerminalIncomingMessagesHandler(getTerminalIO()));
        channel.writeAndFlush("terminalReady");
    }

    @WebkitCall(from = "hterm")
    public void onShellResize(int columns, int rows) {
        channel.writeAndFlush("resizeWindow|" + columns + "|" + rows + "|");
    }

    @WebkitCall (from = "hterm")
    public void command(String command) {
        channel.writeAndFlush(command);
    }

    @WebkitCall(from = "hterm")
    public void copyClipboard(String clipboard) {
            clipboardContent.put(DataFormat.PLAIN_TEXT, clipboard);
            this.clipboard.setContent(clipboardContent);
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
