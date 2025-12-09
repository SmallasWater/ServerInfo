package com.smallaswater.serverinfo;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.ServerStopEvent;
import com.smallaswater.serverinfo.servers.ServerInfo;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Random;

public class ServerStopListener implements Listener {
    
    @EventHandler
    public void onServerStop(ServerStopEvent event) {
        ServerInfoMainClass main = ServerInfoMainClass.getInstance();
        
        if (!main.getConfig().getBoolean("ServerCloseTransfer.enable") || main.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        LinkedList<ServerInfo> servers = new LinkedList<>();
        LinkedList<String> targetServers = new LinkedList<>(main.getConfig().getStringList("ServerCloseTransfer.ServerList"));
        String currentServer = main.getServer().getIp() + ":" + main.getServer().getPort();
        String currentServerName = "";
        if (main.getConfig().getBoolean("ServerCloseTransfer.TransferMode",false)) {
            if (main.getConfig().getBoolean("ServerCloseTransfer.use-WaterdogPE", false)) {
                if (main.getServer().getIp().equals("0.0.0.0")) {
                    currentServer = "127.0.0.1:" + main.getServer().getPort();
                }
            }else if (main.getServer().getIp().equals("0.0.0.0")) {
                currentServer = main.getConfig().getString("ServerCloseTransfer.ip") + ":" + main.getServer().getPort();
            }

            for (ServerInfo targetServer : main.getServerInfos()) {
                boolean isCurrentServer = currentServer.equals(targetServer.getIp() + ":" + targetServer.getPort());
                if (targetServers.contains(targetServer.getName()) && targetServer.onLine() && isCurrentServer) {
                    servers.add(targetServer);
                }
                if (isCurrentServer) {
                    currentServerName = targetServer.getName();
                }
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
                if (main.getConfig().getBoolean("ServerCloseTransfer.use-WaterdogPE", false)) {
                    player.sendMessage(main.getLanguage().getString("player-transfer-serverShutdown").replace("{currentServer}", currentServerName).replace("{targetServer}", targetServer.getName()));
                }
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