package me.unleqitq.commandframework;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public interface ICommandContext {
	
	<T> T getArgument(String name);
	
	<T> T get(String name);
	
	<T> Optional<T> getOptional(String name);
	
	boolean hasArgument(String name);
	
	boolean contains(String name);
	
	boolean declaredArgument(String name);
	
	boolean declaredFlag(String name);
	
	boolean getFlag(String name);
	
	CommandSender getSender();
	
}
