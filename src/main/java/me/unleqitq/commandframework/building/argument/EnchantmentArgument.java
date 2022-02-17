package me.unleqitq.commandframework.building.argument;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;

public class EnchantmentArgument extends FrameworkArgument<Enchantment> {
	
	public EnchantmentArgument(Builder builder) {
		super(builder);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Enchantment> {
		
		public Builder(String name) {
			super(name, (c, a) -> {
				if (a.contains(":")) {
					return Enchantment.getByKey(NamespacedKey.fromString(a.toLowerCase()));
				}
				else {
					return Enchantment.getByKey(NamespacedKey.minecraft(a.toLowerCase()));
				}
			}, (c, a) -> new ArrayList<>(Arrays.stream(Enchantment.values()).map(Enchantment::getKey).map(
					NamespacedKey::asString).filter(s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public EnchantmentArgument build() {
			return new EnchantmentArgument(this);
		}
		
	}
	
}
