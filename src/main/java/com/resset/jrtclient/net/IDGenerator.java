package com.resset.jrtclient.net;
import java.util.UUID;

public final class IDGenerator {

    public static UUID generateUUID() {
        // TODO figure out better generation of host unique values
        return UUID.randomUUID();
    }
}
