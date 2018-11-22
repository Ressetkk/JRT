package com.resset.jrt;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShellListener implements Runnable {
    private TextArea console;
    private InputStreamReader isr;

    public ShellListener(TextArea textArea, InputStream in) {
        this.console = textArea;
        this.isr = new InputStreamReader(in, Charsets.UTF_8);
    }

    public void run() {
        try {
            char[] buffer = new char[1024];
            int read;

            // TODO write basic Windows output parsing
            while ((read = isr.read(buffer)) != -1) {
                String stream = new String(buffer, 0, read);

                boolean isBackspace = false;
                if (stream.equals("\b\u0020\b")) {
                    isBackspace = true;
                }

                // this shit splits first occurrences of \b escape code from the rest of the stream
                // so we know where to move caret
                for (byte b:
                     stream.getBytes()) {
                    System.out.printf("%02X | ", b);
                }
                System.out.println();
                System.out.println(stream);
                Pattern pattern = Pattern.compile("^(\b+)(.*)");
                Matcher mathcher = pattern.matcher(stream);

                if (mathcher.find()) {
                    String firstBacks = mathcher.group(1);
                    stream = mathcher.group(2);
                    moveCaretLeft(firstBacks.length(), !Strings.isNullOrEmpty(stream));
                    if (isBackspace)
                        continue;
                }

                boolean eraseLine = false;
                if (stream.contains("\u001B[K")) {
                    eraseLine = true;
                }

                boolean middleErase = false;
                int numBacks = 0;
                if (stream.contains("\u001B[P")) {
                    middleErase = true;
                    numBacks = stream.replaceAll("[^\b]", "").length();
                }

                if (stream.equals("\u001B[C")) {
                    moveCaretRight(1);
                }

                String res = removeEscapes(stream);

                if (!res.isEmpty()) {
                    final boolean finalMiddleErase = middleErase;
                    final int finalNumBacks = numBacks;
                    Platform.runLater(() -> {
                        if (finalMiddleErase) {
                            console.insertText(console.getCaretPosition(), res);
                            console.positionCaret(console.getCaretPosition() - finalNumBacks);
                        } else {
                            console.appendText(res);
                        }
                    });
                } else if (eraseLine) {
                    Platform.runLater(() -> {
                        console.deleteText(console.getCaretPosition(), console.getLength());
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeEscapes(String source) {
        String target = source.replaceAll("\u001B[\\(\\)][AB012]", "");

        target = target.replaceAll("\u001B\\[\\?*\\d*;*\\d*[a-zA-Z]", "");

        target = target.replaceAll("\u001B[><=A-Z]", "");

        target = target.replaceAll("\\[\\d*;*\\d*m", "");

        target = target.replaceAll("\u000F", "");

        target = target.replaceAll("\u0007", "");

        target = target.replaceAll("\b", "");

        return target;
    }

    private void moveCaretRight(int count) {
        Platform.runLater(() -> console.positionCaret(console.getCaretPosition() + count));
    }

    private void moveCaretLeft(int count, boolean delete) {
        Platform.runLater(() ->{
            console.positionCaret(console.getCaretPosition() - count);
            if (delete) {
                console.deleteText(console.getCaretPosition(), console.getLength());
            }
        });
    }
}
