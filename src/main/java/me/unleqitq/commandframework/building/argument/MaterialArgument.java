package me.unleqitq.commandframework.building.argument;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Arrays;

public class MaterialArgument extends FrameworkArgument<Material> {
	
	public MaterialArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, Material defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Material> {
		
		public Builder(String name) {
			super(name, (c, a) -> Material.matchMaterial(a),
					(c, a) -> Arrays.stream(Material.values()).map(Material::getKey).map(NamespacedKey::getKey).filter(
							s -> s.toLowerCase().startsWith(a.toLowerCase())).toList());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public MaterialArgument build() {
			return new MaterialArgument(this);
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
