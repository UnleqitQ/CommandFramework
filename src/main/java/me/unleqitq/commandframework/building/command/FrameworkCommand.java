package me.unleqitq.commandframework.building.command;

import me.unleqitq.commandframework.ICommandContext;
import me.unleqitq.commandframework.building.FrameworkCommandElement;
import me.unleqitq.commandframework.building.argument.FrameworkArgument;
import me.unleqitq.commandframework.building.flag.FrameworkFlag;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FrameworkCommand<T extends CommandSender> {
	
	private Builder<T> builder;
	
	protected Class<T> senderClass;
	protected String name;
	protected String description;
	protected CommandHandler handler;
	protected String permission;
	protected String[] aliases;
	
	@Nullable
	protected FrameworkCommand<T> previous;
	
	protected List<FrameworkCommandElement> elements = new ArrayList<>();
	
	public FrameworkCommand(Builder<T> builder) {
		this.builder = builder;
		this.senderClass = builder.senderClass;
		this.name = builder.name;
		this.description = builder.description;
		this.handler = builder.handler;
		this.permission = builder.permission;
		this.aliases = builder.aliases;
		for (FrameworkCommandElement.Builder elementBuilder : builder.elements) {
			elements.add(elementBuilder.build());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> getSenderClass() {
		return senderClass;
	}
	
	public String getDescription() {
		return description;
	}
	
	public CommandHandler getHandler() {
		return handler;
	}
	
	public FrameworkCommand<T> getPrevious() {
		return previous;
	}
	
	public List<FrameworkCommandElement> getElements() {
		return elements;
	}
	
	public Builder<T> getBuilder() {
		return builder;
	}
	
	public static Builder<CommandSender> commandBuilder(String name) {
		return new Builder<>(CommandSender.class, name);
	}
	
	public static Builder<Player> playerCommandBuilder(String name) {
		return new Builder<>(Player.class, name);
	}
	
	public static Builder<ConsoleCommandSender> consoleCommandBuilder(String name) {
		return new Builder<>(ConsoleCommandSender.class, name);
	}
	
	public static Builder<CommandSender> commandBuilder(String name, String... aliases) {
		Builder<CommandSender> builder = new Builder<>(CommandSender.class, name);
		builder.aliases = aliases;
		return builder;
	}
	
	public static Builder<Player> playerCommandBuilder(String name, String... aliases) {
		Builder<Player> builder = new Builder<>(Player.class, name);
		builder.aliases = aliases;
		return builder;
	}
	
	public static Builder<ConsoleCommandSender> consoleCommandBuilder(String name, String... aliases) {
		Builder<ConsoleCommandSender> builder = new Builder<>(ConsoleCommandSender.class, name);
		builder.aliases = aliases;
		return builder;
	}
	
	public static class Builder<T extends CommandSender> {
		
		protected Class<T> senderClass;
		
		@Nullable
		protected Builder parent = null;
		protected String name;
		protected String description = "";
		protected CommandHandler handler = null;
		protected String permission = null;
		protected String[] aliases = new String[0];
		
		protected List<FrameworkCommandElement.Builder> elements = new ArrayList<>();
		
		public Builder(Builder parent, String name) {
			this.senderClass = parent.senderClass;
			this.parent = parent;
			this.name = name;
		}
		
		public Builder(Builder parent, Class<T> senderClass, String name) {
			this.senderClass = senderClass;
			this.parent = parent;
			this.name = name;
		}
		
		public Builder(Class<T> senderClass, String name) {
			this.senderClass = senderClass;
			this.name = name;
		}
		
		public Builder<T> handler(CommandHandler handler) {
			this.handler = handler;
			return this;
		}
		
		public Builder<T> argument(FrameworkArgument.Builder argumentBuilder) {
			elements.add(argumentBuilder);
			return this;
		}
		
		public Builder<T> argument(FrameworkArgument.Builder argumentBuilder, String description) {
			elements.add(argumentBuilder.clone().setDescription(description));
			return this;
		}
		
		public Builder<T> flag(FrameworkFlag.Builder flagBuilder) {
			elements.add(flagBuilder);
			return this;
		}
		
		public Builder<T> element(FrameworkCommandElement.Builder elementBuilder) {
			elements.add(elementBuilder);
			return this;
		}
		
		public Builder<T> permission(String permission) {
			this.permission = permission;
			return this;
		}
		
		public Builder<T> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Class<T> getSenderClass() {
			return senderClass;
		}
		
		@Nullable
		public Builder getParent() {
			return parent;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getName() {
			return name;
		}
		
		public FrameworkCommand<T> build() {
			return new FrameworkCommand<>(this);
		}
		
		public Builder<T> subCommand(String name) {
			return new Builder<>(this, name);
		}
		
		public Builder<Player> subPlayerCommand(String name) {
			return new Builder<>(this, Player.class, name);
		}
		
		public Builder<ConsoleCommandSender> subConsoleCommand(String name) {
			return new Builder<>(this, ConsoleCommandSender.class, name);
		}
		
		@Override
		protected FrameworkCommand.Builder<T> clone() {
			Builder<T> builder = new Builder<>(parent, senderClass, name);
			builder.permission = permission;
			builder.elements = elements;
			builder.description = description;
			builder.handler = handler;
			return builder;
		}
		
	}
	
	@FunctionalInterface
	public interface CommandHandler {
		
		void execute(ICommandContext context);
		
	}
	
}
