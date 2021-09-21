package com.smallaswater.serverinfo.utils;

import cn.nukkit.Player;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;
import tip.utils.variables.BaseVariable;

/**
 * @author SmallasWater
 */
public class TipsVariable extends BaseVariable {
    public TipsVariable(Player player) {
        super(player);
    }

    @Override
    public void strReplace() {

        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            if(info.onLine()) {
                addStrReplaceString("{ServerInfoPlayer@" + info.getCallback() + "}", info.getPlayer() + "");
                addStrReplaceString("{ServerInfoMaxPlayer@" + info.getCallback() + "}", info.getMaxPlayer() + "");
            }else{
                addStrReplaceString("{ServerInfoPlayer@" + info.getCallback() + "}", "服务器离线");
                addStrReplaceString("{ServerInfoMaxPlayer@" + info.getCallback() + "}", "服务器离线");
            }

        }
        addStrReplaceString("{ServerInfoPlayer}",ServerInfoMainClass.getInstance().getAllPlayerSize()+"");

    }


}
