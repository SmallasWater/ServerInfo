package com.smallaswater.serverinfo.servers;

import lombok.Data;

/**
 * @author SmallasWater
 * Create on 2021/7/14 8:16
 * Package com.smallaswater.serverinfo.servers
 */
@Data
public class ServerInfo {

    private String callback;

    private String ip;

    private int port;

    private int player;

    private int maxPlayer;


    public ServerInfo(String callback,String ip,int port){
        this.callback = callback;
        this.ip = ip;
        this.port = port;
    }

    public void update(String[] data){
        if(data.length > 0){
            player = Integer.parseInt(data[4]);
            maxPlayer = Integer.parseInt(data[5]);
        }else{
            player = -1;
            maxPlayer = -1;
        }
    }

    public boolean onLine(){
        return player != -1;
    }

    public String toButtonText(){
        if(onLine()){
            return callback+"\n&e"+player+" &7/&6 "+maxPlayer+"  &a在线";
        }else{
            return callback+"\n&c服务器离线";
        }
    }



}
