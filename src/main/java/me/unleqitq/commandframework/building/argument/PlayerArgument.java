package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerArgument extends FrameworkArgument<Player> {
	
	public PlayerArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, Player defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Player> {
		
		public Builder(String name) {
			super(name, (c, a) -> Bukkit.getPlayer(a),
					(c, a) -> new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(p -> {
						if (c.getSender() instanceof ConsoleCommandSender)
							return true;
						return !CommandFramework.isVanished((Player) c.getSender(), p);
					}).map(Player::getName).filter(s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
			errorMessage("Player is not online");
			check((c, a) -> {
				Player p = Bukkit.getPlayer(a);
				if (p == null)
					return false;
				if (c.getSender() instanceof ConsoleCommandSender)
					return true;
				return !CommandFramework.isVanished((Player) c.getSender(), p);
			});
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public PlayerArgument build() {
			return new PlayerArgument(this);
		}
		
		@Override
		public Builder clone() {
			Builder builder = new Builder(name);
			builder.optional = this.optional;
			builder.parser = this.parser;
			builder.defaultValue = this.defaultValue;
			builder.tabCompleteProvider = this.tabCompleteProvider;
			builder.description = this.description;
			return builder;
		}
		
	}
	
}
