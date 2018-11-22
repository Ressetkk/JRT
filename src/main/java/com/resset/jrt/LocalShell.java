package com.resset.jrt;

import com.google.common.base.Charsets;
import com.pty4j.PtyProcess;
import javafx.application.Platform;
import javafx.scene.web.WebView;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LocalShell extends Shell {
    private PtyProcess process;
    private final String[] commands;

    public LocalShell(WebView webView, String[] cmd) {
        super(webView);
        this.commands = cmd;
    }

    public void command(String text) {
        try {
            getOutputWriterProperty().write(text);
            getOutputWriterProperty().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminalReady() {
        Platform.runLater(() -> {
            try {
                initProcess();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initProcess() throws IOException {
        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.remove("TERM_PROGRAM"); // for OS X
        envs.put("TERM", "xterm");
        // TODO change directory regarding remote OS
        this.process = PtyProcess.exec(commands, envs, System.getProperty("user.home"));
        setInputReaderProperty(new BufferedReader(new InputStreamReader(process.getInputStream(), Charsets.UTF_8)));
        setErrorReaderProperty(new BufferedReader(new InputStreamReader(process.getErrorStream(), Charsets.UTF_8)));
        setOutputWriterProperty(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));
    }

    @Override

    // TODO it's a bit hardcoded. Safe shell closing is needed
    public void disconnect() {
        try {
            getOutputWriterProperty().close();
            getInputReaderProperty().close();
            getErrorReaderProperty().close();
        } catch (IOException e) {

        }
        process.destroy();
    }
}
