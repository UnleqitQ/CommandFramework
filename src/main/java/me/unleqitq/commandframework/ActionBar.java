package me.unleqitq.commandframework;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBar {
	
	public static void sendActionBar(@NotNull CommandContext context, String startElement,
									 String endElement) {
		Player player = (Player) context.getSender();
		String usage = context.commandNode.getActionBarUsage(startElement, endElement,
				new boolean[] {false}, true);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(usage));
	}
	
}
