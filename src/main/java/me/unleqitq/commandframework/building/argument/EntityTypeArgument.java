package me.unleqitq.commandframework.building.argument;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;

public class EntityTypeArgument extends FrameworkArgument<EntityType> {
	
	public EntityTypeArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, EntityType defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<EntityType> {
		
		public Builder(String name) {
			super(name, "Entity", (c, a) -> EntityType.fromName(a.toLowerCase()), (c, a) -> new ArrayList<>(
					Arrays.stream(EntityType.values()).filter(e -> e != EntityType.UNKNOWN).map(EntityType::getKey).map(
							NamespacedKey::getKey).filter(s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public EntityTypeArgument build() {
			return new EntityTypeArgument(this);
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
