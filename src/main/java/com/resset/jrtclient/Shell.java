package com.resset.jrtclient;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.Reader;
import java.io.Writer;

public class Shell {
    private final ObjectProperty<Reader> inputReaderProperty;
    private final ObjectProperty<Reader> errorReaderProperty;
    private final ObjectProperty<Writer> outputWriterProperty;
    private WebView shellWindow;
//    private final LinkedBlockingQueue<String> commandQueue;

    public Shell(WebView webView) {
        // TODO We do not know to which OS we connect. Automatic shell choice based on remote OS is needed

        this.shellWindow = webView;

        this.inputReaderProperty = new SimpleObjectProperty<>();
        this.errorReaderProperty = new SimpleObjectProperty<>();
        this.outputWriterProperty = new SimpleObjectProperty<>();

        inputReaderProperty.addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(() -> {
                printStream(newValue);
            });
            thread.start();
        });

        errorReaderProperty.addListener((observable, oldValue, newValue) -> {
            Thread thread = new Thread(() -> {
                printStream(newValue);
            });
            thread.start();
        });

        shellWindow.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            getWindow().setMember("app", this);
        });
        getWebEngine().load(Shell.class.getResource("/hterm/hterm.html").toExternalForm());
    }

    public void onTerminalReady() {

    }

    protected void printStream(Reader bufferedReader) {
        try {
            int read;
            final char[] buffer = new char[1024];

            while((read = bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                final StringBuilder builder = new StringBuilder(read);
                builder.append(buffer, 0, read);
//                print(builder.toString());
                Platform.runLater(() -> getTerminalIO().call("print", builder.toString()));
            }

        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    public WebEngine getWebEngine () {
        return shellWindow.getEngine();
    }

    public JSObject getWindow() {
        return (JSObject) getWebEngine().executeScript("window");
    }

    private JSObject getTerminalIO() {
        return (JSObject) getWebEngine().executeScript("t.io");
    }

    public Reader getInputReaderProperty() {
        return inputReaderProperty.get();
    }

    public void setInputReaderProperty(Reader inputReaderProperty) {
        this.inputReaderProperty.set(inputReaderProperty);
    }

    public ObjectProperty<Reader> inputReaderPropertyProperty() {
        return inputReaderProperty;
    }

    public Reader getErrorReaderProperty() {
        return errorReaderProperty.get();
    }

    public void setErrorReaderProperty(Reader errorReaderProperty) {
        this.errorReaderProperty.set(errorReaderProperty);
    }

    public ObjectProperty<Reader> errorReaderPropertyProperty() {
        return errorReaderProperty;
    }

    public Writer getOutputWriterProperty() {
        return outputWriterProperty.get();
    }

    public void setOutputWriterProperty(Writer outputWriterProperty) {
        this.outputWriterProperty.set(outputWriterProperty);
    }

    public ObjectProperty<Writer> outputWriterPropertyProperty() {
        return outputWriterProperty;
    }

    public void disconnect() {

    }
}
