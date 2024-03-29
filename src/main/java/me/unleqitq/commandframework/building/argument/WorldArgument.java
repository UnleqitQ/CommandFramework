package me.unleqitq.commandframework.building.argument;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;

public class WorldArgument extends FrameworkArgument<World> {
	
	public WorldArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, World defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<World> {
		
		public Builder(String name) {
			super(name, "World", (c, a) -> Bukkit.getWorld(a), (c, a) -> new ArrayList<>(
					Bukkit.getWorlds().stream().map(World::getName).filter(
							s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public WorldArgument build() {
			return new WorldArgument(this);
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
