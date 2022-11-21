package me.unleqitq.commandframework.utils;

import me.unleqitq.commandframework.utils.ComponentWrapper;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandUtils {
	
	public static void printMissingPermission(CommandSender sender, String permission) {
		sender.spigot().sendMessage(new ComponentWrapper(
				new TextComponent("§4You have no permission to do that!")).hoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text("§4Missing Permission:\n§6" + permission))).component());
	}
	
	public static CommandMap getCommandMap() {
		try {
			Server server = Bukkit.getServer();
			Class<? extends Server> serverClass = server.getClass();
			Field commandMapField = serverClass.getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			return (CommandMap) commandMapField.get(server);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static OfflinePlayer getCachedOfflinePlayer(String name) {
		return Arrays.stream(Bukkit.getOfflinePlayers())
				.filter(op -> op.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}
	
}
