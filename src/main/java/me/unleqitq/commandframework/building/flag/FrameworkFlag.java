package me.unleqitq.commandframework.building.flag;

import me.unleqitq.commandframework.building.FrameworkCommandElement;

public class FrameworkFlag extends FrameworkCommandElement {
	
	protected char symbol;
	
	public FrameworkFlag(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
	}
	
	public static Builder of(String name) {
		return new FrameworkFlag.Builder(name, 'a');
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	public static class Builder extends FrameworkCommandElement.Builder {
		
		protected char symbol;
		
		public Builder(String name, char symbol) {
			super(name);
			this.symbol = symbol;
		}
		
		protected Builder setSymbol(char symbol) {
			this.symbol = symbol;
			return this;
		}
		
		protected Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setDescription(String description) {
			super.setDescription(description);
			return this;
		}
		
		public Builder withDescription(String description) {
			super.setDescription(description);
			return this;
		}
		
		public FrameworkFlag build() {
			return new FrameworkFlag(this);
		}
		
	}
	
}
