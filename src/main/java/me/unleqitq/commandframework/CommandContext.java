package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.argument.FrameworkArgument;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class CommandContext implements ICommandContext {
	
	String rawCommand;
	CommandNode commandNode;
	Map<String, String> arguments = new HashMap<>();
	Map<String, Boolean> flags = new HashMap<>();
	CommandSender sender;
	
	public CommandContext(CommandSender sender, String rawCommand) {
		this.sender = sender;
		this.rawCommand = rawCommand;
	}
	
	public void setCommandNode(CommandNode commandNode) {
		this.commandNode = commandNode;
	}
	
	public <T> T getArgument(String name) {
		String sarg = getStringArgument(name);
		FrameworkArgument<T> argument = (FrameworkArgument<T>) commandNode.allArguments().get(name);
		if (sarg == null)
			if (argument.isOptional())
				return argument.getDefaultValue();
			else
				return null;
		return argument.getParser().parse(this, sarg);
	}
	
	@Override
	public <T> T get(String name) {
		return getArgument(name);
	}
	
	@Override
	public <T> Optional<T> getOptional(String name) {
		if (hasArgument("name"))
			return Optional.of(get(name));
		else
			return Optional.empty();
	}
	
	public String getStringArgument(String name) {
		return arguments.get(name);
	}
	
	public boolean hasArgument(String name) {
		return declaredArgument(name) && get(name) != null;
	}
	
	public boolean contains(String name) {
		return arguments.get(name) != null;
	}
	
	public boolean declaredArgument(String name) {
		return arguments.containsKey(name);
	}
	
	public boolean declaredFlag(String name) {
		return flags.containsKey(name);
	}
	
	public boolean getFlag(String name) {
		return declaredFlag(name) && flags.get(name) != null && flags.get(name);
	}
	
	public void setFlag(String name) {
		this.flags.put(name, true);
	}
	
	public void setArgument(String name, String value) {
		this.arguments.put(name, value);
	}
	
	public CommandSender getSender() {
		return sender;
	}
	
	public <T> T getOrSupplyDefault(String name, Supplier<T> supplier) {
		if (hasArgument(name))
			return get(name);
		else
			return supplier.get();
	}
	
	public <T> T getOrDefault(String name, T defaultValue) {
		if (hasArgument(name))
			return get(name);
		else
			return defaultValue;
	}
	
	public String getRawCommand() {
		return rawCommand;
	}
	
	public void unsetFlag(String name) {
		this.flags.put(name, false);
	}
	
	@Override
	public String toString() {
		return String.format("CommandContext{RawCommand=%s\nSender=%s\nArguments=%s\nFlags=%s}", rawCommand, sender,
				arguments, flags);
	}
	
}
