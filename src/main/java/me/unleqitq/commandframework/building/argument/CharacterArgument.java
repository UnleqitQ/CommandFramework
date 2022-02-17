package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;
import java.util.List;

public class CharacterArgument extends FrameworkArgument<Character> {
	
	public CharacterArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, char defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Character> {
		
		public Builder(String name) {
			super(name, (c, a) -> a.charAt(0), (c, a) -> {
				List<String> l = new ArrayList<>();
				for (int i = 0; i < 256; i++) {
					char chr = (char) i;
					if (Character.isAlphabetic(chr) || Character.isDigit(chr))
						l.add(Character.toString(chr));
				}
				return l;
			});
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public CharacterArgument build() {
			return new CharacterArgument(this);
		}
		
	}
	
}
