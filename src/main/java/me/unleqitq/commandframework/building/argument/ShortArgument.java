package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;

import java.util.ArrayList;

public class ShortArgument extends FrameworkArgument<Short> {
	
	protected short minimum;
	protected short maximum;
	
	public ShortArgument(Builder builder) {
		super(builder);
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, short defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	@Override
	public boolean test(ICommandContext context, String argument) {
		short v = Short.parseShort(argument);
		return minimum <= v && v <= maximum;
	}
	
	@Override
	public String errorMessage() {
		return "Value of " + name + " has to be between " + minimum + " and " + maximum;
	}
	
	public static class Builder extends FrameworkArgument.Builder<Short> {
		
		protected short minimum = Short.MIN_VALUE;
		protected short maximum = Short.MAX_VALUE;
		
		public Builder(String name) {
			super(name, "Short", (c, a) -> Short.parseShort(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder withMin(int minimum) {
			this.minimum = (short) minimum;
			return this;
		}
		
		public Builder withMax(int maximum) {
			this.maximum = (short) maximum;
			return this;
		}
		
		public ShortArgument build() {
			return new ShortArgument(this);
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
