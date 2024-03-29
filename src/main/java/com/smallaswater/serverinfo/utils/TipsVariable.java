package com.smallaswater.serverinfo.utils;

import cn.nukkit.Player;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import tip.utils.variables.BaseVariable;

import java.util.Map;

/**
 * @author SmallasWater
 */
public class TipsVariable extends BaseVariable {
    public TipsVariable(Player player) {
        super(player);
    }

    @Override
    public void strReplace() {
        for (Map.Entry<String, String> entry : ServerInfoMainClass.getInstance().getVariableUpdateTask().getVariables().entrySet()) {
            this.addStrReplaceString(entry.getKey(), entry.getValue());
        }
    }

}
