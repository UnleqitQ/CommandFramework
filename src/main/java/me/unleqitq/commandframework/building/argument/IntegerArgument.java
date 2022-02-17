package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;

public class IntegerArgument extends FrameworkArgument<Integer> {
	
	public IntegerArgument(Builder builder) {
		super(builder);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Integer> {
		
		public Builder(String name) {
			super(name, (c, a) -> Integer.parseInt(a), (c, a) -> new ArrayList<>());
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public IntegerArgument build() {
			return new IntegerArgument(this);
		}
		
	}
	
}
