package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.argument.FrameworkArgument;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandContext implements ICommandContext {
	
	CommandNode commandNode;
	Map<String, String> arguments = new HashMap<>();
	Map<String, Boolean> flags = new HashMap<>();
	CommandSender sender;
	
	public CommandContext(CommandSender sender) {
		this.sender = sender;
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
	
	public String getStringArgument(String name) {
		return arguments.get(name);
	}
	
	public boolean hasArgument(String name) {
		return arguments.get(name) != null;
	}
	
	public boolean declaredArgument(String name) {
		return arguments.containsKey(name);
	}
	
	public boolean declaredFlag(String name) {
		return flags.containsKey(name);
	}
	
	public boolean getFlag(String name) {
		return flags.get(name);
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
	
}
