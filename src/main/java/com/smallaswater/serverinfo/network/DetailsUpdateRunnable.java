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
 * 基于查询协议的服务器详细信息更新线程
 *
 * @author LT_Name
 */
public class DetailsUpdateRunnable implements Runnable {

    private static final int sessionId = ServerInfoMainClass.RANDOM.nextInt(1000);

    private String name;
    private String host;
    private int port;

    DetailsUpdateRunnable(ServerInfo info) {
        this.name = info.getName();
        this.host = info.getIp();
        this.port = info.getPort();
    }

    @Override
    public void run() {
        BinaryStream binaryStream;
        try {
            //handshake 握手包
            binaryStream = new BinaryStream();
            binaryStream.putByte((byte) 0xfe);
            binaryStream.putByte((byte) 0xfd);
            binaryStream.putByte(QueryHandler.HANDSHAKE);
            binaryStream.putInt(sessionId);
            binaryStream.putByte((byte) 0);
            binaryStream = this.sendAndReceive(binaryStream);
            if (binaryStream.getByte() == QueryHandler.HANDSHAKE && binaryStream.getInt() == sessionId) {
                byte[] token = new byte[] {(byte) binaryStream.getByte(), (byte) binaryStream.getByte(), (byte) binaryStream.getByte(), (byte) binaryStream.getByte()};

                //statistic 统计包
                binaryStream = new BinaryStream();
                binaryStream.putByte((byte) 0xfe);
                binaryStream.putByte((byte) 0xfd);
                binaryStream.putByte(QueryHandler.STATISTICS);
                binaryStream.putInt(sessionId);
                for (byte t : token) {
                    binaryStream.putByte(t);
                }
                //要求返回长数据类型 FF FF FF 01
                binaryStream.putByte((byte) 0xff);
                binaryStream.putByte((byte) 0xff);
                binaryStream.putByte((byte) 0xff);
                binaryStream.putByte((byte) 0x01);

                binaryStream = this.sendAndReceive(binaryStream);

                if (binaryStream.getByte() == QueryHandler.STATISTICS && binaryStream.getInt() == sessionId) {
                    this.call(this.name, binaryStream.get());
                }
            }else {
                this.call(this.name, new String[0]);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.call(this.name, new String[0]);
        }
    }

    public BinaryStream sendAndReceive(BinaryStream binaryStream) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        try {
            socket.setSoTimeout(5000);
            DatagramPacket packet = new DatagramPacket(Arrays.copyOf(binaryStream.getBuffer(), 64), 64, InetAddress.getByName(this.host), this.port);
            socket.send(packet);
            socket.receive(packet);
            return new BinaryStream(packet.getData());
        } finally {
            socket.close();
        }
    }

    private void call(String name, byte[] data) {
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.getName().equals(name)) {
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
