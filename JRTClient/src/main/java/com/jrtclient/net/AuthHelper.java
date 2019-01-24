package com.jrtclient.net;

public class AuthHelper {

    private static String pass;
    private static String id;

    public static void setPass(String pass) {
        AuthHelper.pass = pass;
    }

    public static void setId(String id) {
        AuthHelper.id = id;
    }

    public static boolean Authenticate(String remoteId, String remotePass) {
        return (id.equals(remoteId)) && (pass.equals(remotePass));
    }
}
