package com.smallaswater.serverinfo.windows;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.ServerStopEvent;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Random;

public class ServerStopListener implements Listener {
    
    @EventHandler
    public void onServerStop(ServerStopEvent event) {
        ServerInfoMainClass main = ServerInfoMainClass.getInstance();
        
        if (!main.getConfig().getBoolean("ServerCloseTransfer.enable") ||
                main.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        LinkedList<ServerInfo> servers = new LinkedList<>();
        LinkedList<String> strings = new LinkedList<>(main.getConfig().getStringList("ServerCloseTransfer.ServerList"));
        String currentServer = main.getServer().getIp() + ":" + main.getServer().getPort();
        String currentServerName = "";
        for (ServerInfo targetServer : main.getServerInfos()) {
            if (strings.contains(targetServer.getName()) && targetServer.onLine() && !targetServer.isFull() && !currentServer.equals(targetServer.getIp() + ":" + targetServer.getPort())) {
                servers.add(targetServer);
            }
            if (currentServer.equals(targetServer.getIp() + ":" + targetServer.getPort())) {
                currentServerName = targetServer.getName();
            }
        }

        for (Player player : main.getServer().getOnlinePlayers().values()) {
            player.sendTitle(
                    main.getConfig().getString("ServerCloseTransfer.showTitle.title"),
                    main.getConfig().getString("ServerCloseTransfer.showTitle.subTitle"),
                    10, 100, 20
            );
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ip = main.getConfig().getString("ServerCloseTransfer.ip");
        int port = main.getConfig().getInt("ServerCloseTransfer.port");

        for (Player player : main.getServer().getOnlinePlayers().values()) {
            ServerInfo targetServer = servers.get(new Random().nextInt(servers.size()));
            if (main.getConfig().getBoolean("ServerCloseTransfer.TransferMode",false)) {
                ip = targetServer.getIp();
                port = targetServer.getPort();
                player.sendMessage(main.getLanguage().getString("player-transfer-serverShutdown").replace("{currentServer}", currentServerName).replace("{targetServer}", targetServer.getName()));
            }
            player.transfer( new InetSocketAddress(ip, port) );
        }
        try {
            //让服务器发送完数据包再关闭
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
