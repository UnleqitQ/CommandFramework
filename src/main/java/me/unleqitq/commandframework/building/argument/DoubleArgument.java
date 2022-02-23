package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;

public class DoubleArgument extends FrameworkArgument<Double> {
	
	public DoubleArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, double defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Double> {
		
		public Builder(String name) {
			super(name, (c, a) -> Double.parseDouble(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public DoubleArgument build() {
			return new DoubleArgument(this);
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
