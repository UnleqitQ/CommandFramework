package me.unleqitq.commandframework.building.argument;

import java.util.ArrayList;
import java.util.Arrays;

public class EnumArgument<E extends Enum<E>> extends FrameworkArgument<E> {
	
	Class<E> argumentEnum;
	
	public EnumArgument(Builder<E> builder) {
		super(builder);
	}
	
	public static <E extends Enum<E>> Builder<E> of(String name, Class<E> argumentEnum) {
		return new Builder<>(name, argumentEnum);
	}
	
	public static <E extends Enum<E>> Builder<E> optional(String name, Class<E> argumentEnum, E defaultValue) {
		return (Builder<E>) new Builder<>(name, argumentEnum).optional(defaultValue);
	}
	
	public static class Builder<E extends Enum<E>> extends FrameworkArgument.Builder<E> {
		
		Class<E> argumentEnum;
		
		public Builder(String name, Class<E> argumentEnum) {
			super(name, (c, a) -> null, (c, a) -> null);
			parser((c, a) -> Arrays.stream(Builder.this.argumentEnum.getEnumConstants()).filter(
					e -> e.name().equalsIgnoreCase(a)).findFirst().orElse(null));
			tabComplete((c, a) -> new ArrayList<>(
					Arrays.stream(Builder.this.argumentEnum.getEnumConstants()).map(Enum::name).filter(
							s -> s.toLowerCase().startsWith(a.toLowerCase())).toList()));
		}
		
		public Builder<E> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public EnumArgument<E> build() {
			return new EnumArgument<E>(this);
		}
		
		@Override
		public Builder clone() {
			Builder builder = new Builder(name, argumentEnum);
			builder.optional = this.optional;
			builder.parser = this.parser;
			builder.defaultValue = this.defaultValue;
			builder.tabCompleteProvider = this.tabCompleteProvider;
			builder.description = this.description;
			return builder;
		}
		
	}
	
}
