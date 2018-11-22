package com.resset.jrt;

import javafx.scene.control.TextArea;

import java.io.IOException;

public class ShellController {
    private Shell process;
    private KeyboardListener keyboardListener;
    private ShellListener shellListener;
    private Thread shellThread;

    public ShellController(TextArea textArea, String[] commands) throws IOException {
        // TODO We do not know to which OS we connect. Automatic shell choice based on remote OS is needed

        this.process = new Shell(commands);
        this.keyboardListener = new KeyboardListener(process.getProcessOutputStream(), textArea);
        this.shellListener = new ShellListener(textArea, process.getProcessInputStream());
        this.shellThread = new Thread(shellListener);
        shellThread.start();
    }

    public Shell getProcess() {
        return process;
    }

    public void disconnect() {
        try {
            keyboardListener.getPrintStream().write(new byte[] {3, 4});
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.getProcess().destroy();
        shellThread.interrupt();
    }
}
