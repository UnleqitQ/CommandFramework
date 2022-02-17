package me.unleqitq.commandframework.building;

import org.jetbrains.annotations.NotNull;

public class FrameworkCommandElement {
	
	@NotNull
	private Builder builder;
	@NotNull
	protected String name;
	@NotNull
	protected String description;
	
	public FrameworkCommandElement(@NotNull Builder builder) {
		this.builder = builder;
		this.name = builder.name;
		this.description = builder.description;
	}
	
	@NotNull
	public String getName() {
		return name;
	}
	
	@NotNull
	public String getDescription() {
		return description;
	}
	
	public static abstract class Builder {
		
		@NotNull
		protected String name;
		@NotNull
		protected String description = "";
		
		public Builder(String name) {
			this.name = name;
		}
		
		private <S extends Builder> S setName(String name) {
			this.name = name;
			return (S) this;
		}
		
		public <S extends Builder> S setDescription(String description) {
			this.description = description;
			return (S) this;
		}
		
		public abstract FrameworkCommandElement build();
		
	}
	
}
