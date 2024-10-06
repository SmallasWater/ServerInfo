package com.smallaswater.serverinfo.windows;

import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 管理GUI窗口
 * 单独的类用于解决GameCore依赖不存在时报错问题
 */
public class CreateAdminWindow {

    public static void showAdminMain(@NotNull Player player) {
        Config language = ServerInfoMainClass.getInstance().getLanguage();
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(TextFormat.colorize('&', language.getString("menu-admin")));

        simple.addButton(TextFormat.colorize('&', language.getString("menu-admin-button-addServer")), CreateAdminWindow::showAdminAddServer);
        simple.addButton(TextFormat.colorize('&', language.getString("menu-admin-button-removeServer")), CreateAdminWindow::showAdminRemoveServer);

        player.showFormWindow(simple);
    }

    public static void showAdminAddServer(@NotNull Player player) {
        Config language = ServerInfoMainClass.getInstance().getLanguage();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(TextFormat.colorize('&', language.getString("menu-adminAddServer")));

        custom.addElement(new ElementInput(TextFormat.colorize('&', language.getString("menu-adminAddServer-name")))); //0
        custom.addElement(new ElementInput(TextFormat.colorize('&', language.getString("menu-adminAddServer-group")))); //1
        custom.addElement(new ElementInput(TextFormat.colorize('&', language.getString("menu-adminAddServer-ip")))); //2
        custom.addElement(new ElementInput(TextFormat.colorize('&', language.getString("menu-adminAddServer-port")))); //3

        custom.onResponded((formResponseCustom, cp) -> {
            String name = formResponseCustom.getInputResponse(0);
            String group = formResponseCustom.getInputResponse(1);
            String ip = formResponseCustom.getInputResponse(2);
            String port = formResponseCustom.getInputResponse(3);

            //检查重复
            for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
                if (info.getName().equals(name)) {
                    sendTipReturnMenu(
                            player,
                            TextFormat.colorize('&', language.getString("menu-adminAddServer-error-repeat-name")),
                            CreateAdminWindow::showAdminAddServer
                    );
                    return;
                } else if (info.getIp().equals(ip) && info.getPort() == Integer.parseInt(port)) {
                    sendTipReturnMenu(
                            player,
                            TextFormat.colorize('&', language.getString("menu-adminAddServer-error-repeat-ip")),
                            CreateAdminWindow::showAdminAddServer
                    );
                    return;
                }
            }

            ServerInfo serverInfo = new ServerInfo(name, group, ip, Integer.parseInt(port));
            ServerInfoMainClass.getInstance().getServerInfos().add(serverInfo);

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("group", group);
            map.put("ip", ip);
            map.put("port", port);

            Config config = ServerInfoMainClass.getInstance().getConfig();
            List<Map> list = config.getMapList("server-info");
            list.add(map);
            config.set("server-info", list);
            config.save();
            sendTipReturnMenu(player, TextFormat.colorize('&', language.getString("menu-adminAddServer-complete")), CreateAdminWindow::showAdminMain);
        });
        custom.onClosed(CreateAdminWindow::showAdminMain);

        custom.showToPlayer(player);
    }

    public static void showAdminRemoveServer(@NotNull Player player) {
        Config language = ServerInfoMainClass.getInstance().getLanguage();
        String title = TextFormat.colorize('&', language.getString("menu-adminRemoveServer"));
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(title, title);

        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            simple.addButton(TextFormat.colorize('&', info.getName()), (cp) -> {
                String text = TextFormat.colorize('&', language.getString("menu-adminRemoveServer-confirm").replace("{server}", info.getName()));
                AdvancedFormWindowModal modal = new AdvancedFormWindowModal(
                        text, text,
                        TextFormat.colorize('&', language.getString("menu-button-confirm")),
                        TextFormat.colorize('&', language.getString("menu-button-return"))
                );
                modal.onClickedTrue(cp2 -> {
                    ServerInfoMainClass.getInstance().getServerInfos().remove(info);
                    Config config = ServerInfoMainClass.getInstance().getConfig();
                    List<Map> list = config.getMapList("server-info");
                    for (Map map : new ArrayList<>(list)) {
                        if (map.get("name").equals(info.getName()) &&
                                map.get("group").equals(info.getGroup()) &&
                                map.get("ip").equals(info.getIp()) &&
                                map.get("port").equals(info.getPort())) {
                            list.remove(map);
                            break;
                        }
                    }
                    config.set("server-info", list);
                    config.save();
                    sendTipReturnMenu(cp2, TextFormat.colorize('&', language.getString("menu-adminRemoveServer-complete").replace("{server}", info.getName())), CreateAdminWindow::showAdminRemoveServer);
                });
                modal.onClickedFalse(CreateAdminWindow::showAdminRemoveServer);
                cp.showFormWindow(modal);
            });
        }

        simple.showToPlayer(player);
    }

    private static void sendTipReturnMenu(Player player, String text, Consumer<Player> consumer) {
        Config language = ServerInfoMainClass.getInstance().getLanguage();
        AdvancedFormWindowModal modal = new AdvancedFormWindowModal(
                "ServerInfo", text,
                TextFormat.colorize('&', language.getString("menu-button-confirm")),
                TextFormat.colorize('&', language.getString("menu-button-close"))
        );

        modal.onClickedTrue(consumer);

        modal.showToPlayer(player);
    }

}
