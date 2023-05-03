package com.smallaswater.serverinfo.network;

import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author SmallasWater
 * Create on 2021/7/13 17:40
 * Package com.smallaswater.serverinfo.runnable
 */
public class SimpleUpdateRunnable implements Runnable {

    private final String callback;
    private final String host;
    private final int port;

    private static final byte[] MOTD_DATA = new byte[]{1, 0, 0, 0, 0, 0, 3, 106, 7, 0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120, -100, 116, 22, -68};

    SimpleUpdateRunnable(ServerInfo info) {
        this.callback = info.getName();
        this.host = info.getIp();
        this.port = info.getPort();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            DatagramPacket packet = new DatagramPacket(Arrays.copyOf(MOTD_DATA, 1024), 1024, InetAddress.getByName(this.host), this.port);
            socket.send(packet);
            socket.receive(packet);
            this.call(this.callback, new String(packet.getData(), 35, packet.getLength(), StandardCharsets.UTF_8).split(";"));
        } catch (Throwable e) {
            this.call(this.callback, new String[0]);
            if (socket != null) {
                socket.close();
            }
        }
    }

    private void call(String callback, String[] data) {
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.getName().equals(callback)) {
                info.update(data);
            }
        }
    }

}
