package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;

public class FloatArgument extends FrameworkArgument<Float> {
	
	protected float minimum;
	protected float maximum;
	
	public FloatArgument(Builder builder) {
		super(builder);
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, float defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	@Override
	public boolean test(String argument) {
		float v = Float.parseFloat(argument);
		return minimum <= v && v <= maximum;
	}
	
	@Override
	public String errorMessage() {
		return "Value of " + name + " has to be between " + minimum + " and " + maximum;
	}
	
	public static class Builder extends FrameworkArgument.Builder<Float> {
		
		protected float minimum = Float.NEGATIVE_INFINITY;
		protected float maximum = Float.POSITIVE_INFINITY;
		
		public Builder(String name) {
			super(name, (c, a) -> Float.parseFloat(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder withMin(float minimum) {
			this.minimum = minimum;
			return this;
		}
		
		public Builder withMax(float maximum) {
			this.maximum = maximum;
			return this;
		}
		
		public FloatArgument build() {
			return new FloatArgument(this);
		}
		
		@Override
		public Builder clone() {
			Builder builder = new Builder(name);
			builder.optional = this.optional;
			builder.parser = this.parser;
			builder.defaultValue = this.defaultValue;
			builder.tabCompleteProvider = this.tabCompleteProvider;
			builder.description = this.description;
			builder.minimum = this.minimum;
			builder.maximum = this.maximum;
			return builder;
		}
		
	}
	
}
