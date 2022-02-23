package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;
import me.unleqitq.commandframework.building.FrameworkCommandElement;

import java.util.List;

public abstract class FrameworkArgument<T> extends FrameworkCommandElement {
	
	protected TabCompleteProvider tabCompleteProvider;
	protected Parser<T> parser;
	protected boolean optional;
	protected T defaultValue;
	
	public FrameworkArgument(Builder<T> builder) {
		super(builder);
		this.tabCompleteProvider = builder.tabCompleteProvider;
		this.parser = builder.parser;
		this.optional = builder.optional;
		this.defaultValue = builder.defaultValue;
	}
	
	public TabCompleteProvider getTabCompleteProvider() {
		return tabCompleteProvider;
	}
	
	public Parser<T> getParser() {
		return parser;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	public void optional() {
		this.optional = true;
	}
	
	public boolean test(String argument) {
		return true;
	}
	
	public String errorMessage() {
		return "Wrong argument";
	}
	
	public static abstract class Builder<T> extends FrameworkCommandElement.Builder {
		
		protected TabCompleteProvider tabCompleteProvider;
		protected Parser<T> parser;
		protected boolean optional;
		protected T defaultValue;
		
		public Builder(String name, Parser<T> defaultParser, TabCompleteProvider defaultTabCompleteProvider) {
			super(name);
			parser = defaultParser;
			tabCompleteProvider = defaultTabCompleteProvider;
		}
		
		public Builder<T> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder<T> parser(Parser<T> parser) {
			this.parser = parser;
			return this;
		}
		
		public Builder<T> tabComplete(TabCompleteProvider tabCompleteProvider) {
			this.tabCompleteProvider = tabCompleteProvider;
			return this;
		}
		
		public boolean isOptional() {
			return optional;
		}
		
		public Builder<T> optional(T defaultValue) {
			this.optional = true;
			this.defaultValue = defaultValue;
			return this;
		}
		
		public Builder<T> optional() {
			return optional(null);
		}
		
		public abstract FrameworkArgument<T> build();
		
		@Override
		public abstract FrameworkArgument.Builder<T> clone();
		
	}
	
	@FunctionalInterface
	public interface TabCompleteProvider {
		
		List<String> tabComplete(ICommandContext context, String currentArgument);
		
	}
	
	@FunctionalInterface
	public interface Parser<T> {
		
		T parse(ICommandContext context, String argument);
		
	}
	
}
