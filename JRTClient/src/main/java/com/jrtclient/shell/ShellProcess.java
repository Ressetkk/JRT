package com.jrtclient.shell;

import com.google.common.base.Charsets;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import io.netty.channel.ChannelHandlerContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShellProcess implements Shell {
    private PtyProcess process;
    private String[] commands = null;
    private ChannelHandlerContext ctx;

    private final ObjectProperty<Reader> inputReaderProperty;
    private final ObjectProperty<Reader> errorReaderProperty;
    private final ObjectProperty<Writer> outputWriterProperty;

    public ShellProcess(ChannelHandlerContext ctx) {
        String os = System.getProperty("os.name").toLowerCase();

        if ((os.contains("mac")) || (os.contains("linux"))) {
            this.commands = new String[] {"/bin/bash", "-i"};
        }
        else if (os.contains("win")) {
            this.commands = new String[] {"powershell.exe", "" };
        }

        this.inputReaderProperty = new SimpleObjectProperty<>();
        this.errorReaderProperty = new SimpleObjectProperty<>();
        this.outputWriterProperty = new SimpleObjectProperty<>();
        this.ctx = ctx;

        inputReaderProperty.addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(() -> read(newValue));
            thread.start();
        });

        errorReaderProperty.addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(() -> read(newValue));
            thread.start();
        });
    }

    @Override
    public void initProcess() throws IOException {
        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.remove("TERM_PROGRAM"); // for OS X
        envs.put("TERM", "xterm");
        process = PtyProcess.exec(commands, envs, System.getProperty("user.home"));

        setInputReaderProperty(new BufferedReader(new InputStreamReader(process.getInputStream(), Charsets.UTF_8)));
        setErrorReaderProperty(new BufferedReader(new InputStreamReader(process.getErrorStream(), Charsets.UTF_8)));
        setOutputWriterProperty(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));
    }

    @Override
    public void command(String command) {
        // this shit writes to my pty process
        try {
            getOutputWriterProperty().write(command);
            getOutputWriterProperty().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(Reader bufferedReader) {
        // this shit reads from my pty process
        try {
            int read;
            final char[] buffer = new char[1024];
            
            while((read = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                final StringBuilder builder = new StringBuilder(read);
                builder.append(buffer, 0, read);
                ctx.writeAndFlush(builder.toString());
            }

        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            getOutputWriterProperty().close();
            getInputReaderProperty().close();
            getErrorReaderProperty().close();
        } catch (IOException ignored) {

        }
        process.destroy();
    }

    @Override
    public void resizeShell(int columns, int rows) {
        process.setWinSize(new WinSize(columns, rows));
        System.out.printf("%dx%d", columns, rows);
    }

    public Reader getInputReaderProperty() {
        return inputReaderProperty.get();
    }

    public ObjectProperty<Reader> inputReaderPropertyProperty() {
        return inputReaderProperty;
    }

    public Reader getErrorReaderProperty() {
        return errorReaderProperty.get();
    }

    public ObjectProperty<Reader> errorReaderPropertyProperty() {
        return errorReaderProperty;
    }

    public Writer getOutputWriterProperty() {
        return outputWriterProperty.get();
    }

    public void setInputReaderProperty(Reader inputReaderProperty) {
        this.inputReaderProperty.set(inputReaderProperty);
    }

    public void setErrorReaderProperty(Reader errorReaderProperty) {
        this.errorReaderProperty.set(errorReaderProperty);
    }

    public void setOutputWriterProperty(Writer outputWriterProperty) {
        this.outputWriterProperty.set(outputWriterProperty);
    }
}
