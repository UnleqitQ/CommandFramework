package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;

public class StringArgument extends FrameworkArgument<String> {
	
	public StringArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name) {
		return (Builder) new Builder(name).optional();
	}
	
	public static class Builder extends FrameworkArgument.Builder<String> {
		
		public Builder(String name) {
			super(name, (c, a) -> a, (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public StringArgument build() {
			return new StringArgument(this);
		}
		
	}
	
}
