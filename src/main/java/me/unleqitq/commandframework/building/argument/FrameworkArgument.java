package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;
import me.unleqitq.commandframework.building.FrameworkCommandElement;

import java.util.List;
import java.util.function.BiPredicate;

public abstract class FrameworkArgument<T> extends FrameworkCommandElement {
	
	protected TabCompleteProvider tabCompleteProvider;
	protected Parser<T> parser;
	protected boolean optional;
	protected T defaultValue;
	protected BiPredicate<ICommandContext, String> check;
	protected String errorMessage;
	protected String argumentType;
	
	public FrameworkArgument(Builder<T> builder) {
		super(builder);
		this.tabCompleteProvider = builder.tabCompleteProvider;
		this.parser = builder.parser;
		this.optional = builder.optional;
		this.defaultValue = builder.defaultValue;
		this.check = builder.check;
		this.errorMessage = builder.errorMessage;
		this.argumentType = builder.argumentType;
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
	
	public boolean test(ICommandContext c, String argument) {
		return check.test(c, argument);
	}
	
	public String errorMessage() {
		return errorMessage;
	}
	
	public String argumentType() {
		return argumentType;
	}
	
	public static abstract class Builder<T> extends FrameworkCommandElement.Builder {
		
		protected TabCompleteProvider tabCompleteProvider;
		protected Parser<T> parser;
		protected boolean optional;
		protected T defaultValue;
		protected BiPredicate<ICommandContext, String> check = (u, v) -> true;
		protected String errorMessage = "Wrong argument";
		protected String argumentType;
		
		public Builder(String name, String defaultArgumentType, Parser<T> defaultParser,
					   TabCompleteProvider defaultTabCompleteProvider) {
			super(name);
			this.argumentType = defaultArgumentType;
			parser = defaultParser;
			tabCompleteProvider = defaultTabCompleteProvider;
		}
		
		@Deprecated
		public Builder(String name, Parser<T> defaultParser,
					   TabCompleteProvider defaultTabCompleteProvider) {
			this(name, "UNKNOWN", defaultParser, defaultTabCompleteProvider);
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
		
		public Builder<T> argumentType(String argumentType) {
			this.argumentType = argumentType;
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
		
		public void check(BiPredicate<ICommandContext, String> predicate) {
			this.check = predicate;
		}
		
		public void errorMessage(String message) {
			this.errorMessage = message;
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
