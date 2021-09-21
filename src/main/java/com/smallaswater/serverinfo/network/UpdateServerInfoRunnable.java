package com.smallaswater.serverinfo.network;


import cn.nukkit.Server;
import cn.nukkit.network.protocol.PlayerListPacket;
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
        while (true){
            for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
                ServerInfoMainClass.THREAD_POOL.execute(new SendPacketRunnable(info));
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
