package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.CommandContext;
import me.unleqitq.commandframework.ICommandContext;

import java.util.ArrayList;

public class DoubleArgument extends FrameworkArgument<Double> {
	
	protected double minimum;
	protected double maximum;
	
	public DoubleArgument(Builder builder) {
		super(builder);
		this.minimum = builder.minimum;
		this.maximum = builder.maximum;
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, double defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	@Override
	public boolean test(ICommandContext context, String argument) {
		double v = Double.parseDouble(argument);
		return minimum <= v && v <= maximum;
	}
	
	@Override
	public String errorMessage() {
		return "Value of " + name + " has to be between " + minimum + " and " + maximum;
	}
	
	public static class Builder extends FrameworkArgument.Builder<Double> {
		
		protected double minimum = Double.NEGATIVE_INFINITY;
		protected double maximum = Double.POSITIVE_INFINITY;
		
		public Builder(String name) {
			super(name, "Double", (c, a) -> Double.parseDouble(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder withMin(double minimum) {
			this.minimum = minimum;
			return this;
		}
		
		public Builder withMax(double maximum) {
			this.maximum = maximum;
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
			builder.minimum = this.minimum;
			builder.maximum = this.maximum;
			return builder;
		}
		
	}
	
}
