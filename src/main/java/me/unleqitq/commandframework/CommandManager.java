package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.command.FrameworkCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
	
	private static Map<String, CommandNode> rootNodes = new HashMap<>();
	private Plugin plugin;
	
	public CommandManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public CommandNode register(FrameworkCommand.Builder commandBuilder) {
		CommandNode node;
		if (commandBuilder.getParent() == null) {
			if (rootNodes.containsKey(commandBuilder.getName()))
				node = rootNodes.get(commandBuilder.getName());
			else {
				node = new CommandNode(plugin, commandBuilder.build(), null);
				Command prev = Bukkit.getCommandMap().getCommand(commandBuilder.getName());
				if (prev != null)
					prev.unregister(Bukkit.getCommandMap());
				Bukkit.getCommandMap().register("f", node);
			}
			rootNodes.put(commandBuilder.getName(), node);
		}
		else {
			CommandNode parent;
			parent = register0(commandBuilder.getParent());
			
			if (parent.hasChild(commandBuilder.getName()))
				node = parent.getChild(commandBuilder.getName());
			else
				node = new CommandNode(plugin, commandBuilder.build(), parent);
			parent.getChildren().put(commandBuilder.getName(), node);
		}
		updateHelp(node);
		return node;
	}
	
	public CommandNode register0(FrameworkCommand.Builder commandBuilder) {
		CommandNode node;
		if (commandBuilder.getParent() == null) {
			if (rootNodes.containsKey(commandBuilder.getName()))
				node = rootNodes.get(commandBuilder.getName());
			else {
				node = new CommandNode(plugin, commandBuilder.build(), null);
				Command prev = Bukkit.getCommandMap().getCommand(commandBuilder.getName());
				if (prev != null)
					prev.unregister(Bukkit.getCommandMap());
				Bukkit.getCommandMap().register("f", node);
			}
			rootNodes.put(commandBuilder.getName(), node);
		}
		else {
			CommandNode parent;
			parent = register0(commandBuilder.getParent());
			
			if (parent.hasChild(commandBuilder.getName()))
				node = parent.getChild(commandBuilder.getName());
			else
				node = new CommandNode(plugin, commandBuilder.build(), parent);
			parent.getChildren().put(commandBuilder.getName(), node);
		}
		return node;
	}
	
	public void updateHelp(CommandNode node) {
		if (node.getParent() != null) {
			updateHelp(node.getParent());
			return;
		}
		Bukkit.getHelpMap().addTopic(node.getHelpTopic());
	}
	
}
