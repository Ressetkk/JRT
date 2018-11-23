package com.resset.jrtclient;

import java.io.IOException;

public interface Shell {

    public void initProcess() throws IOException;
    public void command(String command);
    public void disconnect();
}
