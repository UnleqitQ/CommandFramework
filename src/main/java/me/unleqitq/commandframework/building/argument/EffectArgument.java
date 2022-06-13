package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.utils.EffectUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class EffectArgument extends FrameworkArgument<PotionEffectType> {
	
	public EffectArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, PotionEffectType defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<PotionEffectType> {
		
		public Builder(String name) {
			super(name, (c, a) -> {
				if (a.contains(":")) {
					return PotionEffectType.getByName(a.toLowerCase());
				}
				else {
					return PotionEffectType.getByName(a.toLowerCase());
				}
			}, (c, a) -> new ArrayList<>(EffectUtils.getEffectTypes().stream().map(PotionEffectType::getName).filter(
					k -> k.toLowerCase().startsWith(a.toLowerCase()) || k.toLowerCase().startsWith(
							a.toLowerCase())).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public EffectArgument build() {
			return new EffectArgument(this);
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
