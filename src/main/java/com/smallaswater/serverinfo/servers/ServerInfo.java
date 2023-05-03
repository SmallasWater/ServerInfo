package com.smallaswater.serverinfo.servers;

import cn.nukkit.utils.BinaryStream;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author SmallasWater
 * Create on 2021/7/14 8:16
 * Package com.smallaswater.serverinfo.servers
 */
@Data
public class ServerInfo {

    private String name;

    private String group;

    private String ip;

    private int port;

    private int player;

    private int maxPlayer;


    public ServerInfo(String name, String group, String ip, int port) {
        this.name = name;
        this.group = group;
        this.ip = ip;
        this.port = port;
    }

    @Deprecated
    public String getCallback() {
        return name;
    }

    @Deprecated
    public void setCallback(String name) {
        this.name = name;
    }

    public void update(byte[] data) {
        //TODO dev
        if (true) {
            return;
        }
        if (data.length == 0) {
            player = -1;
            maxPlayer = -1;
            return;
        }
        BinaryStream binaryStream = new BinaryStream(data);

        String s = new String(binaryStream.getByteArray(), StandardCharsets.UTF_8);
        ServerInfoMainClass.getInstance().getLogger().info(s);

        binaryStream.getByteArray();
        binaryStream.getByte();
        binaryStream.getByte();
        binaryStream.getByte();

        LinkedHashMap<String, String> KVdata = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            String key = new String(binaryStream.getByteArray(), StandardCharsets.UTF_8);
            if (binaryStream.getByte() == 0x00) {
                String value = new String(binaryStream.getByteArray(), StandardCharsets.UTF_8);
                if (binaryStream.getByte() == 0x00) {
                    KVdata.put(key, value);
                }
            }
        }

        ServerInfoMainClass.getInstance().getLogger().info(KVdata.toString());

        binaryStream.getByte(); //0x00
        binaryStream.getByte(); //0x01
        binaryStream.getByteArray(); //"player_".getBytes()
        binaryStream.getByte(); //0x00
        binaryStream.getByte(); //0x00

        ArrayList<String> players = new ArrayList<>();

        while (true) {
            int aByte = binaryStream.getByte();
            if (aByte == 0x00) {
                break;
            }
            players.add(new String(binaryStream.getByteArray(), StandardCharsets.UTF_8));
            if (binaryStream.getByte() != 0x00) {
                break;
            }
        }

        ServerInfoMainClass.getInstance().getLogger().info(players.toString());
    }

    public void update(String[] data) {
        if (data.length > 0) {
            player = Integer.parseInt(data[4]);
            maxPlayer = Integer.parseInt(data[5]);
        } else {
            player = -1;
            maxPlayer = -1;
        }
    }

    /**
     * @return 服务器是否在线
     */
    public boolean onLine() {
        return player != -1;
    }

    public String toButtonText() {
        if (onLine()) {
            return ServerInfoMainClass.getInstance().getLanguage().getString("button-text-online", "")
                    .replace("{server}", name).replace("{player}", player + "").replace("{maxplayer}", maxPlayer + "");
//            return callback+"\n&e"+player+" &7/&6 "+maxPlayer+"  &a在线";
        } else {
            return ServerInfoMainClass.getInstance().getLanguage().getString("button-text-offline", "")
                    .replace("{server}", name);
        }
    }


}
