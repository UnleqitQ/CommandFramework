package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;
import java.util.List;

public class BooleanArgument extends FrameworkArgument<Boolean> {
	
	public BooleanArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, boolean defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Boolean> {
		
		public Builder(String name) {
			super(name, "Boolean", (c, a) -> Boolean.parseBoolean(a), (c, a) -> new ArrayList<>(List.of("true", "false")));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public BooleanArgument build() {
			return new BooleanArgument(this);
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
