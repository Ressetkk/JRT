package com.resset.jrtclient;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public final class IDGenerator {

    public static UUID generateUUID() {
        try {
            byte[] username = System.getProperty("user.name").getBytes();
            StringBuilder userHex = new StringBuilder();
            for (int i = 0; i < username.length; i++) {
                userHex.append(String.format("%02X", username[i]));
            }
            long usernameLong = new BigInteger(userHex.toString(), 16).longValue();

            byte[] macAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuilder macHex = new StringBuilder();
            for (int i = 0; i < macAddress.length; i++) {
                macHex.append(String.format("%02X", macAddress[i]));
            }
            long macAddressLong = new BigInteger(macHex.toString(), 16).longValue();

            return new UUID(usernameLong, macAddressLong);

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
