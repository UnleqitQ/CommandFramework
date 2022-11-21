package me.unleqitq.commandframework.building.argument;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;

public class EnchantmentArgument extends FrameworkArgument<Enchantment> {
	
	public EnchantmentArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, Enchantment defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Enchantment> {
		
		public Builder(String name) {
			super(name, "Enchantment", (c, a) -> {
				if (a.contains(":")) {
					return Enchantment.getByKey(NamespacedKey.fromString(a.toLowerCase()));
				}
				else {
					return Enchantment.getByKey(NamespacedKey.minecraft(a.toLowerCase()));
				}
			}, (c, a) -> new ArrayList<>(Arrays.stream(Enchantment.values()).map(Enchantment::getKey).filter(
					k -> k.getKey().toLowerCase().startsWith(a.toLowerCase()) || k.toString().toLowerCase().startsWith(
							a.toLowerCase())).map(NamespacedKey::toString).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public EnchantmentArgument build() {
			return new EnchantmentArgument(this);
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
