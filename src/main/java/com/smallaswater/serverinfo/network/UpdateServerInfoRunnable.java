package com.smallaswater.serverinfo.network;

import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;



/**
 * @author SmallasWater
 * Create on 2021/7/14 9:03
 * Package com.smallaswater.serverinfo.network
 */
public class UpdateServerInfoRunnable implements Runnable {

    @Override
    public void run() {
        while (ServerInfoMainClass.getInstance().isEnabled()) {
            for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
                if (ServerInfoMainClass.getInstance().isSyncPlayer()) {
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
