package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class TemplateManage {

    // /dominion template_manage <模板名称> [页码]
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(player.getUniqueId(), args[1]);
        if (template == null) {
            Notification.error(sender, "模板 %s 不存在", args[1]);
            return;
        }

        ListView view = ListView.create(10, "/dominion template_manage " + template.getName());
        view.title("模板 " + args[1] + " 权限管理");
        view.navigator(Line.create()
                .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                .append(Button.create("模板列表").setExecuteCommand("/dominion template_list").build())
                .append("模板管理")
        );

        // /dominion template_set_flag <模板名称> <权限名称> <true/false> [页码]

        if (template.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion template_set_flag " + template.getName() + " admin false " + page).build())
                    .append("管理员"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion template_set_flag " + template.getName() + " admin true " + page).build())
                    .append("管理员"));
        }
        for (Flag flag : Flag.getPrivilegeFlagsEnabled()) {
            view.add(createOption(flag, template.getFlagValue(flag), template.getName(), page));
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String templateName, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion template_set_flag " + templateName + " " + flag.getFlagName() + " false " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion template_set_flag " + templateName + " " + flag.getFlagName() + " true " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
