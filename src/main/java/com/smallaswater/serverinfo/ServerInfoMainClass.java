package com.smallaswater.serverinfo;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.server.ServerStopEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.serverinfo.network.UpdateServerInfoRunnable;
import com.smallaswater.serverinfo.servers.ServerInfo;
import com.smallaswater.serverinfo.utils.RsNpcXVariable;
import com.smallaswater.serverinfo.utils.TipsVariable;
import com.smallaswater.serverinfo.windows.CreateWindow;
import lombok.Getter;
import tip.utils.Api;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author SmallasWater
 * Create on 2021/7/13 15:23
 * Package com.smallaswater.serverinfo
 */
public class ServerInfoMainClass extends PluginBase implements Listener {

    private static ServerInfoMainClass instance;

    @Getter
    private ArrayList<ServerInfo> serverInfos = new ArrayList<>();

    public static final ThreadPoolExecutor THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        loadServer();
        this.getLogger().info("服务器信息加载完成");
        THREAD_POOL.execute(new UpdateServerInfoRunnable());
        //注册TIPS变量
        Api.registerVariables("serverInfo", TipsVariable.class);

        this.getServer().getPluginManager().registerEvents(instance, instance);

        try {
            Class.forName("com.smallaswater.npc.variable.VariableManage");
            com.smallaswater.npc.variable.VariableManage.addVariable("ServerInfoVariable", RsNpcXVariable.class);
        } catch (Exception ignored) {

        }

    }

    public void call(String callback,String[] data){
        for(ServerInfo info: serverInfos){
            if(info.getCallback().equals(callback)){
                info.update(data);
            }
        }

    }

    private void loadServer(){
        serverInfos.clear();
        ServerInfo info;
        for(Map map:getConfig().getMapList("server-info")){
            info = new ServerInfo(map.get("name").toString(),map.get("ip").toString(),Integer.parseInt(map.get("port").toString()));
            serverInfos.add(info);
            this.getLogger().info("加载服务器 "+info.getCallback()+" 完成");
        }
    }

    public static ServerInfoMainClass getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0){
            if("reload".equalsIgnoreCase(args[0]) && sender.isOp()){
                saveDefaultConfig();
                reloadConfig();
                loadServer();
                sender.sendMessage("配置文件重载完成");
                return true;
            }
        }
        if(sender instanceof Player){
            CreateWindow.showMenu((Player) sender);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onWindow(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            return;
        }
        if(event.getFormID() == CreateWindow.MENU){
            ServerInfo info = getServerInfos().get(
                    ((FormResponseSimple)event.getResponse()).getClickedButtonId());
            if(info.onLine()){
                Server.getInstance().broadcastMessage(TextFormat.colorize('&',"&e玩家 &a"+event.getPlayer().getName()+" &e前往了 &r"+info.getCallback()+" &e服务器"));
                event.getPlayer().transfer(new InetSocketAddress(info.getIp(),info.getPort()));
            }else{
                event.getPlayer().sendMessage(TextFormat.colorize('&',"&e[&f跨服&e] 服务器离线"));
            }
        }
    }

    @EventHandler
    public void onServerStop(ServerStopEvent event) {
        if (!this.getConfig().getBoolean("ServerCloseTransfer.enable") ||
                this.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }
        for (Player player : this.getServer().getOnlinePlayers().values()) {
            player.sendTitle(
                    this.getConfig().getString("ServerCloseTransfer.showTitle.title"),
                    this.getConfig().getString("ServerCloseTransfer.showTitle.subTitle"),
                    10,
                    100,
                    20
            );
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : this.getServer().getOnlinePlayers().values()) {
            player.transfer(
                    new InetSocketAddress(
                            this.getConfig().getString("ServerCloseTransfer.ip"),
                            this.getConfig().getInt("ServerCloseTransfer.port")
                    )
            );
        }
        try {
            //让服务器发送完数据包再关闭
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
