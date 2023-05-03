package com.smallaswater.serverinfo.utils;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.serverinfo.ServerInfoMainClass;

import java.util.Map;

/**
 * @author lt_name
 */
public class RsNpcVariable extends BaseVariableV2 {

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        for (Map.Entry<String, String> entry : ServerInfoMainClass.getInstance().getVariableUpdateTask().getVariables().entrySet()) {
            this.addVariable(entry.getKey(), entry.getValue());
        }
    }

}
