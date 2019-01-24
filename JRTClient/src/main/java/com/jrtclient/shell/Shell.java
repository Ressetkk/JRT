package com.jrtclient.shell;

import java.io.IOException;

public interface Shell {

    void initProcess() throws IOException;
    void command(String command);
    void disconnect();
    void resizeShell(int columns, int rows);
}
