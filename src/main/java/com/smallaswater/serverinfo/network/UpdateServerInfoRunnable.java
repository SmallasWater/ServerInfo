package com.smallaswater.serverinfo.network;

import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;



/**
 * @author SmallasWater
 * Create on 2021/7/14 9:03
 * Package com.smallaswater.serverinfo.network
 */
public class UpdateServerInfoRunnable implements Runnable {

    private final boolean useDetails;

    public UpdateServerInfoRunnable() {
        this.useDetails = "Details".equalsIgnoreCase(ServerInfoMainClass.getInstance().getConfig().getString("UpdateInfoProvide"));
    }

    @Override
    public void run() {
        while (ServerInfoMainClass.getInstance().isEnabled()) {
            for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
                if (this.useDetails) {
                    ServerInfoMainClass.THREAD_POOL.execute(new DetailsUpdateRunnable(info));
                } else {
                    ServerInfoMainClass.THREAD_POOL.execute(new SimpleUpdateRunnable(info));
                }

            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

    }
}
