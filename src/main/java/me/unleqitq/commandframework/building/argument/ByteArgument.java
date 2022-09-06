package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;

import java.util.ArrayList;

public class ByteArgument extends FrameworkArgument<Byte> {
	
	protected byte minimum;
	protected byte maximum;
	
	public ByteArgument(Builder builder) {
		super(builder);
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, byte defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	@Override
	public boolean test(ICommandContext context, String argument) {
		int v = Byte.parseByte(argument);
		return minimum <= v && v <= maximum;
	}
	
	@Override
	public String errorMessage() {
		return "Value of " + name + " has to be between " + minimum + " and " + maximum;
	}
	
	public static class Builder extends FrameworkArgument.Builder<Byte> {
		
		protected byte minimum = Byte.MIN_VALUE;
		protected byte maximum = Byte.MAX_VALUE;
		
		public Builder(String name) {
			super(name, (c, a) -> Byte.parseByte(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder withMin(int minimum) {
			this.minimum = (byte) minimum;
			return this;
		}
		
		public Builder withMax(int maximum) {
			this.maximum = (byte) maximum;
			return this;
		}
		
		public ByteArgument build() {
			return new ByteArgument(this);
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
