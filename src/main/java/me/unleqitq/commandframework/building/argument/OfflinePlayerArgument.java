package me.unleqitq.commandframework.building.argument;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class OfflinePlayerArgument extends FrameworkArgument<OfflinePlayer> {
	
	public OfflinePlayerArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, OfflinePlayer defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<OfflinePlayer> {
		
		public Builder(String name) {
			super(name, (c, a) -> Bukkit.getOfflinePlayerIfCached(a), (c, a) -> new ArrayList<>(
					Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(
							Objects::nonNull).filter(s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public OfflinePlayerArgument build() {
			return new OfflinePlayerArgument(this);
		}
		
	}
	
}
