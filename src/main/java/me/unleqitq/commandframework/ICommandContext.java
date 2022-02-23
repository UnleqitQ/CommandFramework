package me.unleqitq.commandframework;

import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.function.Supplier;

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
	
	String getRawCommand();
	
	<T> T getOrSupplyDefault(String name, Supplier<T> supplier);
	
	<T> T getOrDefault(String name, T defaultValue);
	
}
