package me.unleqitq.commandframework.building.argument;

import me.unleqitq.commandframework.ICommandContext;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.List;

public class MaterialArgument extends FrameworkArgument<Material> {
	
	public MaterialArgument(Builder builder) {
		super(builder);
	}
	
	public static Builder of(String name) {
		return new Builder(name);
	}
	
	public static Builder optional(String name, Material defaultValue) {
		return (Builder) new Builder(name).optional(defaultValue);
	}
	
	public static class Builder extends FrameworkArgument.Builder<Material> {
		
		public Builder(String name) {
			super(name, new MaterialParser(), new MaterialTabComplete(true, true));
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public MaterialArgument build() {
			return new MaterialArgument(this);
		}
		
		@Override
		public Builder clone() {
			Builder builder = new Builder(name);
			builder.optional = this.optional;
			builder.parser = this.parser;
			builder.defaultValue = this.defaultValue;
			builder.tabCompleteProvider = this.tabCompleteProvider;
			builder.description = this.description;
			return builder;
		}
		
	}
	
	public static class MaterialParser implements Parser<Material> {
		
		@Override
		public Material parse(ICommandContext context, String argument) {
			for (Material material : Material.values()) {
				if (material.getKey().getKey().replaceAll("_", "").equalsIgnoreCase(argument.replaceAll("_", "")))
					return material;
			}
			return null;
		}
		
	}
	
	public static class MaterialTabComplete implements TabCompleteProvider {
		
		boolean nonItems;
		boolean nonBlocks;
		
		public MaterialTabComplete(boolean nonItems, boolean nonBlocks) {
			this.nonItems = nonItems;
			this.nonBlocks = nonBlocks;
		}
		
		@Override
		public List<String> tabComplete(ICommandContext context, String currentArgument) {
			return Arrays.stream(Material.values()).filter(
					m -> (nonItems || m.isItem()) && (nonBlocks || m.isBlock())).filter(m -> {
				String n = m.getKey().getKey();
				String[] ns = n.split("_");
				String[] args = currentArgument.split("_");
				return Arrays.stream(args).allMatch(a -> Arrays.stream(ns).anyMatch(
						n0 -> n0.toLowerCase().replaceAll("_", "").startsWith(n0.toLowerCase().replaceAll("_", ""))));
			}).map(Material::getKey).map(NamespacedKey::getKey).toList();
		}
		
	}
	
}
