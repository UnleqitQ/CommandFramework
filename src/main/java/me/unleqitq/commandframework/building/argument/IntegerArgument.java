package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;

import java.util.ArrayList;

public class IntegerArgument extends FrameworkArgument<Integer> {
	
	protected int minimum;
	protected int maximum;
	
	public IntegerArgument(Builder builder) {
		super(builder);
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, int defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	@Override
	public boolean test(ICommandContext context, String argument) {
		int v = Integer.parseInt(argument);
		return minimum <= v && v <= maximum;
	}
	
	@Override
	public String errorMessage() {
		return "Value of " + name + " has to be between " + minimum + " and " + maximum;
	}
	
	public static class Builder extends FrameworkArgument.Builder<Integer> {
		
		protected int minimum = Integer.MIN_VALUE;
		protected int maximum = Integer.MAX_VALUE;
		
		public Builder(String name) {
			super(name, "Integer", (c, a) -> Integer.parseInt(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder withMin(int minimum) {
			this.minimum = minimum;
			return this;
		}
		
		public Builder withMax(int maximum) {
			this.maximum = maximum;
			return this;
		}
		
		public IntegerArgument build() {
			return new IntegerArgument(this);
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
