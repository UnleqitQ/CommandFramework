package me.unleqitq.commandframework;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBar {
	
	public static void sendActionBar(@NotNull CommandContext context, String startElement, String endElement) {
		if (!(context.getSender() instanceof Player))
			return;
		
		Player player = (Player) context.getSender();
		String usage = context.commandNode.getActionBarUsage(startElement, endElement, new boolean[]{false}, true);
		player.sendActionBar(Component.text(usage));
	}
	
}
