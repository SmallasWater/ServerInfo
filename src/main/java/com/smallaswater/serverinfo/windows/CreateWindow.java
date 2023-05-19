package com.smallaswater.serverinfo.windows;

import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.serverinfo.ServerInfoMainClass;
import com.smallaswater.serverinfo.servers.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author SmallasWater
 * Create on 2021/7/14 9:14
 * Package com.smallaswater.serverinfo.windows
 */
public class CreateWindow {

    public static final int MENU = 0x55011;

    private CreateWindow() {

    }

    public static void showMenu(@NotNull Player player) {
        FormWindowSimple simple = new FormWindowSimple(TextFormat.colorize('&',
                ServerInfoMainClass.getInstance().getLanguage().getString("menu-title"))
                , TextFormat.colorize('&',
                ServerInfoMainClass.getInstance().getLanguage().getString("menu-text").replace("{sum}",
                        ServerInfoMainClass.getInstance().getAllPlayerSize() + "")));
        for (ServerInfo info : ServerInfoMainClass.getInstance().getServerInfos()) {
            simple.addButton(new ElementButton(TextFormat.colorize('&', info.toButtonText())
                    , new ElementButtonImageData("path", "textures/ui/icon_new")));
        }
        if (player.isOp() && ServerInfoMainClass.getInstance().isHasGameCore()) {
            simple.addButton(new ElementButton(TextFormat.colorize('&',
                    ServerInfoMainClass.getInstance().getLanguage().getString("menu-admin"))
                    , new ElementButtonImageData("path", "textures/ui/dev_glyph_color")));
        }
        player.showFormWindow(simple, MENU);
    }

    public static void showAdminMain(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(TextFormat.colorize('&', ServerInfoMainClass.getInstance().getLanguage().getString("menu-admin")));

        simple.addButton("添加服务器", CreateWindow::showAdminAddServer);
        simple.addButton("编辑服务器", (cp) -> {});
        simple.addButton("删除服务器", (cp) -> {});

        player.showFormWindow(simple);
    }

    public static void showAdminAddServer(@NotNull Player player) {
        Config language = ServerInfoMainClass.getInstance().getLanguage();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(TextFormat.colorize('&', language.getString("menu-adminAddServer")));

        custom.addElement(new ElementInput("menu-adminAddServer-name")); //0
        custom.addElement(new ElementInput("menu-adminAddServer-group")); //1
        custom.addElement(new ElementInput("menu-adminAddServer-ip")); //2
        custom.addElement(new ElementInput("menu-adminAddServer-port")); //3

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
                            CreateWindow::showAdminAddServer
                    );
                    return;
                } else if (info.getIp().equals(ip) && info.getPort() == Integer.parseInt(port)) {
                    sendTipReturnMenu(
                            player,
                            TextFormat.colorize('&', language.getString("menu-adminAddServer-error-repeat-ip")),
                            CreateWindow::showAdminAddServer
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
            sendTipReturnMenu(player, TextFormat.colorize('&', language.getString("menu-adminAddServer-complete")), CreateWindow::showAdminMain);
        });
        custom.onClosed(CreateWindow::showAdminMain);

        player.showFormWindow(custom);
    }

    private static void sendTipReturnMenu(Player player, String text, Consumer<Player> consumer) {
        AdvancedFormWindowModal modal = new AdvancedFormWindowModal("ServerInfo", text, "返回", "关闭");

        modal.onClickedTrue(consumer);

        modal.showToPlayer(player);
    }
}
