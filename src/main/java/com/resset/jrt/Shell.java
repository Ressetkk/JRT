package com.resset.jrt;

import com.pty4j.PtyProcess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Shell {
    private PtyProcess process;

    public Shell(String[] cmd) throws IOException {
        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.remove("TERM_PROGRAM"); // for OS X
        envs.put("TERM", "vt102");

        this.process = PtyProcess.exec(cmd, envs, System.getProperty("user.home"));
    }

    public PtyProcess getProcess() {
        return process;
    }

    public InputStream getProcessInputStream() {
        return process.getInputStream();
    }

    public OutputStream getProcessOutputStream() {
        return process.getOutputStream();
    }

}
