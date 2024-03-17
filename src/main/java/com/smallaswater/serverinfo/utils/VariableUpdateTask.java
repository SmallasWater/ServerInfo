package com.smallaswater.serverinfo.utils;

import cn.nukkit.scheduler.PluginTask;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LT_Name
 */
public class VariableUpdateTask extends PluginTask<ServerInfoMainClass> {

    private final ConcurrentHashMap<String, String> variables = new ConcurrentHashMap<>();

    public VariableUpdateTask(ServerInfoMainClass serverInfoMainClass) {
        super(serverInfoMainClass);
    }

    @Override
    public void onRun(int i) {
        this.variables.clear();
        Config language = ServerInfoMainClass.getInstance().getLanguage();

        HashMap<String, Integer> groupPlayer = new HashMap<>();
        HashMap<String, Integer> groupMaxPlayer = new HashMap<>();
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.onLine()) {
                this.addVariable("{ServerInfoPlayer@" + info.getName() + "}", (TextFormat.colorize('&', language.getString("server-status-online")
                    .replace("{player}", String.valueOf(info.getPlayer()))
                )));
                this.addVariable("{ServerInfoMaxPlayer@" + info.getName() + "}", String.valueOf(info.getMaxPlayer()));
                this.addVariable("{ServerInfoPlayerAll@" + info.getName() + "}", (TextFormat.colorize('&', language.getString("server-status-online2")
                    .replace("{player}", String.valueOf(info.getPlayer())).replace("{maxplayer}", String.valueOf(info.getMaxPlayer()))
                )));                
                if (!groupPlayer.containsKey(info.getGroup())) {
                    groupPlayer.put(info.getGroup(), 0);
                }
                groupPlayer.put(info.getGroup(), groupPlayer.get(info.getGroup()) + info.getPlayer());
                if (!groupMaxPlayer.containsKey(info.getGroup())) {
                    groupMaxPlayer.put(info.getGroup(), 0);
                }
                groupMaxPlayer.put(info.getGroup(), groupMaxPlayer.get(info.getGroup()) + info.getMaxPlayer());
            } else {
                String serverOffline = TextFormat.colorize('&', language.getString("server-status-offline"));
                this.addVariable("{ServerInfoPlayer@" + info.getName() + "}", serverOffline);
                this.addVariable("{ServerInfoMaxPlayer@" + info.getName() + "}", serverOffline);
                this.addVariable("{ServerInfoGroupPlayer@" + info.getGroup() + "}", serverOffline);
                this.addVariable("{ServerInfoGroupMaxPlayer@" + info.getGroup() + "}", serverOffline);
                this.addVariable("{ServerInfoPlayerAll@" + info.getName() + "}", serverOffline);
                this.addVariable("{ServerInfoGroupPlayerAll@" + info.getGroup() + "}", serverOffline);
            }
        }
        for (Map.Entry<String, Integer> entry : groupPlayer.entrySet()) {
            this.addVariable("{ServerInfoGroupPlayer@" + entry.getKey() + "}", (TextFormat.colorize('&', language.getString("server-status-online")
            .replace("{player}", String.valueOf(entry.getValue())))));
        }
        for (Map.Entry<String, Integer> entry : groupMaxPlayer.entrySet()) {
            this.addVariable("{ServerInfoGroupMaxPlayer@" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        for (Map.Entry<String, Integer> entry : groupPlayer.entrySet()) {
            for (Map.Entry<String, Integer> entry2 : groupMaxPlayer.entrySet()) {
            this.addVariable("{ServerInfoGroupPlayerAll@" + entry.getKey() + "}", (TextFormat.colorize('&', language.getString("server-status-online2")
                .replace("{player}", String.valueOf(entry.getValue())).replace("{maxplayer}", String.valueOf(entry2.getValue()))
            )));
        }}
        this.addVariable("{ServerInfoPlayer}", String.valueOf(ServerInfoMainClass.getInstance().getAllPlayerSize()));
    }

    protected void addVariable(String key, String value) {
        this.variables.put(key, value);
    }

    public ConcurrentHashMap<String, String> getVariables() {
        return variables;
    }
}