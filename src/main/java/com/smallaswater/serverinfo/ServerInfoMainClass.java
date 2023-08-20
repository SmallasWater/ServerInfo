package com.smallaswater.serverinfo;

import cn.lanink.gamecore.utils.ConfigUtils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.ServerStopEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.serverinfo.network.UpdateServerInfoRunnable;
import com.smallaswater.serverinfo.servers.ServerInfo;
import com.smallaswater.serverinfo.utils.RsNpcVariable;
import com.smallaswater.serverinfo.utils.TipsVariable;
import com.smallaswater.serverinfo.utils.VariableUpdateTask;
import com.smallaswater.serverinfo.windows.CreateWindow;
import lombok.Getter;
import tip.utils.Api;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author SmallasWater
 * Create on 2021/7/13 15:23
 * Package com.smallaswater.serverinfo
 */
public class ServerInfoMainClass extends PluginBase implements Listener {


    public static final ThreadPoolExecutor THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public static final Random RANDOM = new Random();

    @Getter
    private static ServerInfoMainClass instance;

    private Config language;

    @Getter
    private boolean syncPlayer;

    @Getter
    private List<ServerInfo> serverInfos = new CopyOnWriteArrayList<>();

    @Getter
    private VariableUpdateTask variableUpdateTask;

    @Getter
    private boolean hasGameCore = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        try {
            Class.forName("cn.lanink.gamecore.GameCore");
            this.hasGameCore = true;
        } catch (Exception ignored) {

        }
        this.saveDefaultConfig();
        this.saveResource("language.yml");
        this.saveResource("变量说明.txt", true);

        this.reloadConfig();
        this.language = new Config(this.getDataFolder() + "/language.yml", Config.YAML);
        this.syncPlayer = this.getConfig().getBoolean("sync-player", false);

        if (this.hasGameCore) {
            Config configDescription = new Config();
            configDescription.load(this.getResource("ConfigDescription.yml"));
            ConfigUtils.addDescription(this.getConfig(), configDescription);
        }

        this.loadServer();

        this.getLogger().info("服务器信息加载完成");

        this.getServer().getScheduler().scheduleTask(this, () -> { //使用task在服务器启动完成后开始获取信息
            THREAD_POOL.execute(new UpdateServerInfoRunnable());
        });

        this.getServer().getPluginManager().registerEvents(instance, instance);

        boolean needVariableUpdate = false;
        //注册TIPS变量
        try {
            Class.forName("tip.utils.Api");
            Api.registerVariables("ServerInfoVariable", TipsVariable.class);
            needVariableUpdate = true;
        } catch (Exception ignored) {

        }
        //注册RsNPC变量
        try {
            Class.forName("com.smallaswater.npc.variable.BaseVariableV2");
            com.smallaswater.npc.variable.VariableManage.addVariableV2("ServerInfoVariable", RsNpcVariable.class);
            needVariableUpdate = true;
        } catch (Exception ignored) {

        }
        //注册变量更新Task
        if (needVariableUpdate) {
            this.variableUpdateTask = new VariableUpdateTask(this);
            this.getServer().getScheduler().scheduleRepeatingTask(this, this.variableUpdateTask, 100, true);
        }
    }

    public Config getLanguage() {
        return language;
    }

    public int getAllPlayerSize() {
        int maxOnline = this.getServer().getOnlinePlayers().size();
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.onLine()) {
                maxOnline += info.getPlayer();
            }
        }
        return maxOnline;
    }

    public int getAllPlayerSize(String group) {
        int maxOnline = 0;
        for (ServerInfo info : this.getServerInfos(group)) {
            if (info.onLine()) {
                maxOnline += info.getPlayer();
            }
        }
        return maxOnline;
    }

    @Deprecated
    public void call(String name, String[] data) {
        for (ServerInfo info : serverInfos) {
            if (info.getName().equals(name)) {
                info.update(data);
            }
        }
    }

    private void loadServer() {
        this.serverInfos.clear();
        for (Map map : getConfig().getMapList("server-info")) {
            ServerInfo info = new ServerInfo(
                    map.get("name").toString(),
                    map.getOrDefault("group", "default").toString(),
                    map.get("ip").toString(),
                    Integer.parseInt(map.get("port").toString())
            );
            serverInfos.add(info);
            this.getLogger().info("加载服务器 " + info.getName() + " 完成");
        }
    }

    public ArrayList<ServerInfo> getServerInfos(String group) {
        ArrayList<ServerInfo> list = new ArrayList<>();
        for (ServerInfo info : serverInfos) {
            if (info.getGroup().equals(group)) {
                list.add(info);
            }
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
                saveDefaultConfig();
                reloadConfig();
                loadServer();
                sender.sendMessage("配置文件重载完成");
                return true;
            }
        }
        if (sender instanceof Player) {
            CreateWindow.showMenu((Player) sender);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onQueryRegenerateEvent(QueryRegenerateEvent event) {
        if (this.syncPlayer) {
            event.setPlayerCount(this.getAllPlayerSize());
        }
    }

    @EventHandler
    public void onWindow(PlayerFormRespondedEvent event) {
        if (event.wasClosed() || event.getResponse() == null) {
            return;
        }
        if (event.getFormID() == CreateWindow.MENU) {
            int clickedButtonId = ((FormResponseSimple) event.getResponse()).getClickedButtonId();
            if (clickedButtonId >= getServerInfos().size()) {
                CreateWindow.showAdminMain(event.getPlayer());
                return;
            }
            ServerInfo info = getServerInfos().get(clickedButtonId);
            if (info.onLine()) {
                Server.getInstance().broadcastMessage(TextFormat.colorize('&', language.getString("player-transfer-text").replace("{server}", info.getName()))
                        .replace("{name}", event.getPlayer().getName()));
                event.getPlayer().transfer(new InetSocketAddress(info.getIp(), info.getPort()));
            } else {
                event.getPlayer().sendMessage(TextFormat.colorize('&', language.getString("player-transfer-off")));
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
