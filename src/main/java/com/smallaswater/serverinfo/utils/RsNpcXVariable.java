package com.smallaswater.serverinfo.utils;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariable;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;

/**
 * @author lt_name
 */
public class RsNpcXVariable extends BaseVariable {

    @Override
    public String stringReplace(Player player, String s) {
        // {ServerInfoPlayer@服务器名字}
        // {ServerInfoMaxPlayer@服务器名字}
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if (info.onLine()) {
                s = s.replace("{ServerInfoPlayer@" + info.getCallback() + "}", info.getPlayer() + "")
                        .replace("{ServerInfoMaxPlayer@" + info.getCallback() + "}", info.getMaxPlayer() + "");
            }else {
                s = s.replace("{ServerInfoPlayer@" + info.getCallback() + "}", "服务器离线")
                        .replace("{ServerInfoMaxPlayer@" + info.getCallback() + "}", "服务器离线");
            }
        }
        return s.replace("{ServerInfoPlayer}", ServerInfoMainClass.getInstance().getAllPlayerSize()+"");
    }

    public static class RsNpcXVariableV2 extends BaseVariableV2 {

        @Override
        public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
            for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
                if (info.onLine()) {
                    this.addVariable("{ServerInfoPlayer@" + info.getCallback() + "}", info.getPlayer() + "");
                    this.addVariable("{ServerInfoMaxPlayer@" + info.getCallback() + "}", info.getMaxPlayer() + "");
                }else {
                    this.addVariable("{ServerInfoPlayer@" + info.getCallback() + "}", "服务器离线");
                    this.addVariable("{ServerInfoMaxPlayer@" + info.getCallback() + "}", "服务器离线");
                }
            }
            this.addVariable("{ServerInfoPlayer}", ServerInfoMainClass.getInstance().getAllPlayerSize()+"");
        }

    }

}
