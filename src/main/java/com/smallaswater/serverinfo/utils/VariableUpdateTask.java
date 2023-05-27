package com.smallaswater.serverinfo.utils;

import cn.nukkit.scheduler.PluginTask;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;

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

        HashMap<String, Integer> groupPlayer = new HashMap<>();
        HashMap<String, Integer> groupMaxPlayer = new HashMap<>();
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.onLine()) {
                this.addVariable("{ServerInfoPlayer@" + info.getName() + "}", String.valueOf(info.getPlayer()));
                this.addVariable("{ServerInfoMaxPlayer@" + info.getName() + "}", String.valueOf(info.getMaxPlayer()));
                if (!groupPlayer.containsKey(info.getGroup())) {
                    groupPlayer.put(info.getGroup(), 0);
                }
                groupPlayer.put(info.getGroup(), groupPlayer.get(info.getGroup()) + info.getPlayer());
                if (!groupMaxPlayer.containsKey(info.getGroup())) {
                    groupMaxPlayer.put(info.getGroup(), 0);
                }
                groupMaxPlayer.put(info.getGroup(), groupMaxPlayer.get(info.getGroup()) + info.getMaxPlayer());
            } else {
                this.addVariable("{ServerInfoPlayer@" + info.getName() + "}", "服务器离线");
                this.addVariable("{ServerInfoMaxPlayer@" + info.getName() + "}", "服务器离线");
                this.addVariable("{ServerInfoGroupPlayer@" + info.getGroup() + "}", "服务器离线");
                this.addVariable("{ServerInfoGroupMaxPlayer@" + info.getGroup() + "}", "服务器离线");
            }
        }
        for (Map.Entry<String, Integer> entry : groupPlayer.entrySet()) {
            this.addVariable("{ServerInfoGroupPlayer@" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        for (Map.Entry<String, Integer> entry : groupMaxPlayer.entrySet()) {
            this.addVariable("{ServerInfoGroupMaxPlayer@" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        this.addVariable("{ServerInfoPlayer}", String.valueOf(ServerInfoMainClass.getInstance().getAllPlayerSize()));
    }

    protected void addVariable(String key, String value) {
        this.variables.put(key, value);
    }

    public ConcurrentHashMap<String, String> getVariables() {
        return variables;
    }
}
