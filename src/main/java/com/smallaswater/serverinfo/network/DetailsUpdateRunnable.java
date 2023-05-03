package com.smallaswater.serverinfo.network;

import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.utils.BinaryStream;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author LT_Name
 */
public class DetailsUpdateRunnable implements Runnable {

    private static final int sessionId = ServerInfoMainClass.RANDOM.nextInt(1000);

    private String callback;
    private String host;
    private int port;

    DetailsUpdateRunnable(ServerInfo info) {
        this.callback = info.getName();
        this.host = info.getIp();
        this.port = info.getPort();
    }

    @Override
    public void run() {
        BinaryStream binaryStream;
        try {
            //handshake
            binaryStream = new BinaryStream();
            binaryStream.putByte((byte) 0xfe);
            binaryStream.putByte((byte) 0xfd);
            binaryStream.putByte(QueryHandler.HANDSHAKE);
            binaryStream.putInt(sessionId);
            binaryStream.putByte((byte) 0);
            binaryStream = this.sendAndReceive(binaryStream);
            if (binaryStream.getByte() == QueryHandler.HANDSHAKE && binaryStream.getInt() == sessionId) {
                //TODO 找出token错误的原因
                byte[] token = Arrays.copyOf(binaryStream.getByteArray(), 4);

                ServerInfoMainClass.getInstance().getLogger().info("token: " + Arrays.toString(token));

                binaryStream = new BinaryStream();
                binaryStream.putByte((byte) 0xfe);
                binaryStream.putByte((byte) 0xfd);
                binaryStream.putByte(QueryHandler.STATISTICS);
                binaryStream.putInt(sessionId);
                binaryStream.putByteArray(token);

                binaryStream = this.sendAndReceive(binaryStream);

                if (binaryStream.getByte() == QueryHandler.STATISTICS && binaryStream.getInt() == sessionId) {
                    byte[] data = binaryStream.getByteArray();
                    this.call(this.callback, data);
                }
            }else {
                this.call(this.callback, new String[0]);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.call(this.callback, new String[0]);
        }
    }

    public BinaryStream sendAndReceive(BinaryStream binaryStream) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        DatagramPacket packet = new DatagramPacket(Arrays.copyOf(binaryStream.getBuffer(), 64), 64, InetAddress.getByName(this.host), this.port);
        socket.send(packet);
        socket.receive(packet);
        BinaryStream stream = new BinaryStream(packet.getData());
        socket.close();
        return stream;
    }

    private void call(String callback, byte[] data) {
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.getName().equals(callback)) {
                info.update(data);
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
