package me.unleqitq.commandframework;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class CommandUtils {
	
	public static void printMissingPermission(CommandSender sender, String permission) {
		sender.sendMessage(Component.text("§4You have no permission to do that!").hoverEvent(
				HoverEvent.showText(Component.text("§4Missing Permission:\n§6" + permission))));
	}
	
}
