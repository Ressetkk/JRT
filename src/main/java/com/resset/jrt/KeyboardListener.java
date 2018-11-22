package com.resset.jrt;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class KeyboardListener {

    private TextArea console;
    private PrintStream printStream;
    private static Map<Integer, byte[]> keyMap;

    public KeyboardListener(OutputStream processOutputStream, TextArea textArea) {
        this.console = textArea;
        this.printStream = new PrintStream(processOutputStream, true);
        setKeyListener();
    }
    // TODO needs a rewrite to include Windows Escape Sequences
    public void setKeyListener() {
        console.addEventFilter(KeyEvent.KEY_RELEASED, key ->
        {
            key.consume();
        });
        console.addEventFilter(KeyEvent.KEY_TYPED, key ->
        {
            key.consume();
        });

        console.addEventFilter(KeyEvent.KEY_PRESSED, key ->
        {
            byte[] keyCode;
            keyCode = getKeyCode(key);
            if (keyCode != null) {
                try {
                    printStream.write(keyCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                printStream.print(key.getText());
            }
            key.consume();
        });
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    @Nullable
    private byte[] getKeyCode(KeyEvent key) {
        if (key.getCode() == KeyCode.BACK_SPACE) {
            return keyMap.get(72);
        } else if (key.getCode() == KeyCode.LEFT) {
            return (keyMap.get(37));
        } else if (key.getCode() == KeyCode.UP) {
            return (keyMap.get(38));
        } else if (key.getCode() == KeyCode.RIGHT) {
            return (keyMap.get(39));
        } else if (key.getCode() == KeyCode.DOWN) {
            return (keyMap.get(40));
        } else if (key.isControlDown() && key.getCode() == KeyCode.L) {
            console.clear();
            return (keyMap.get(76));
        } else if (key.isControlDown() && key.getCode() == KeyCode.D) {
            return (keyMap.get(68));
        }
        return null;
    }

    static {
        keyMap = new HashMap<>();
        //ESC
        keyMap.put(27, new byte[]{(byte) 0x1b});
        //ENTER
        keyMap.put(13, new byte[]{(byte) 0x0d});
        //LEFT
        keyMap.put(37, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x44});
        //UP
        keyMap.put(38, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x41});
        //RIGHT
        keyMap.put(39, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x43});
        //DOWN
        keyMap.put(40, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x42});
        //DEL
        keyMap.put(8, new byte[]{(byte) 0x7f});
        //TAB
        keyMap.put(9, new byte[]{(byte) 0x09});
        //CTR
        keyMap.put(17, new byte[]{});
        //CTR-A
        keyMap.put(65, new byte[]{(byte) 0x01});
        //CTR-B
        keyMap.put(66, new byte[]{(byte) 0x02});
        //CTR-C
        keyMap.put(67, new byte[]{(byte) 0x03});
        //CTR-D
        keyMap.put(68, new byte[]{(byte) 0x04});
        //CTR-E
        keyMap.put(69, new byte[]{(byte) 0x05});
        //CTR-F
        keyMap.put(70, new byte[]{(byte) 0x06});
        //CTR-G
        keyMap.put(71, new byte[]{(byte) 0x07});
        //BACKSPACE
        keyMap.put(72, new byte[]{(byte) 0x08});
        //CTR-I
        keyMap.put(73, new byte[]{(byte) 0x09});
        //CTR-J
        keyMap.put(74, new byte[]{(byte) 0x0A});
        //CTR-K
        keyMap.put(75, new byte[]{(byte) 0x0B});
        //CTR-L
        keyMap.put(76, new byte[]{(byte) 0x0C});
        //CTR-M
        keyMap.put(77, new byte[]{(byte) 0x0D});
        //CTR-N
        keyMap.put(78, new byte[]{(byte) 0x0E});
        //CTR-O
        keyMap.put(79, new byte[]{(byte) 0x0F});
        //CTR-P
        keyMap.put(80, new byte[]{(byte) 0x10});
        //CTR-Q
        keyMap.put(81, new byte[]{(byte) 0x11});
        //CTR-R
        keyMap.put(82, new byte[]{(byte) 0x12});
        //CTR-S
        keyMap.put(83, new byte[]{(byte) 0x13});
        //CTR-T
        keyMap.put(84, new byte[]{(byte) 0x14});
        //CTR-U
        keyMap.put(85, new byte[]{(byte) 0x15});
        //CTR-V
        keyMap.put(86, new byte[]{(byte) 0x16});
        //CTR-W
        keyMap.put(87, new byte[]{(byte) 0x17});
        //CTR-X
        keyMap.put(88, new byte[]{(byte) 0x18});
        //CTR-Y
        keyMap.put(89, new byte[]{(byte) 0x19});
        //CTR-Z
        keyMap.put(90, new byte[]{(byte) 0x1A});
        //CTR-[
        keyMap.put(219, new byte[]{(byte) 0x1B});
        //CTR-]
        keyMap.put(221, new byte[]{(byte) 0x1D});
    }
}
