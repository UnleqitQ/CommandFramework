package me.unleqitq.commandframework;

import org.bukkit.command.CommandSender;

public interface ICommandContext {
	
	<T> T getArgument(String name);
	
	boolean hasArgument(String name);
	
	boolean declaredArgument(String name);
	
	boolean declaredFlag(String name);
	
	boolean getFlag(String name);
	
	CommandSender getSender();
	
}