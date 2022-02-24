package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.FrameworkCommandElement;
import me.unleqitq.commandframework.building.argument.FrameworkArgument;
import me.unleqitq.commandframework.building.argument.StringArrayArgument;
import me.unleqitq.commandframework.building.command.FrameworkCommand;
import me.unleqitq.commandframework.building.flag.FrameworkFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.IndexHelpTopic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandNode extends Command {
	
	@NotNull
	private FrameworkCommand command;
	
	private Map<String, CommandNode> children = new HashMap<>();
	@Nullable
	private CommandNode parent;
	
	public CommandNode(FrameworkCommand command, @Nullable CommandNode parent) {
		super(command.getName());
		this.command = command;
		this.parent = parent;
		
		setAliases(Arrays.stream(command.getAliases()).toList());
		setDescription(command.getDescription());
		setUsage(getStringUsage(true));
	}
	
	public HelpTopic getHelpTopic() {
		if (children.values().size() == 0) {
			return new GenericCommandHelpTopic(this);
		}
		else {
			Set<HelpTopic> helpTopics = new HashSet<>();
			for (CommandNode child : children.values()) {
				helpTopics.add(child.getHelpTopic());
			}
			return new IndexHelpTopic(getCommandName(), command.getDescription(), getPermission(), helpTopics);
		}
	}
	
	public FrameworkCommand getCommand() {
		return command;
	}
	
	public CommandNode getParent() {
		return parent;
	}
	
	public Map<String, CommandNode> getChildren() {
		return children;
	}
	
	public String getCommandName() {
		return command.getName();
	}
	
	@Nullable
	public CommandNode getChild(String name) {
		return children.get(name);
	}
	
	public boolean hasChild(String name) {
		return children.containsKey(name);
	}
	
	public Map<String, FrameworkArgument<?>> allArguments() {
		Map<String, FrameworkArgument<?>> argumentMap = new HashMap<>();
		if (parent != null) {
			argumentMap.putAll(parent.allArguments());
		}
		for (Object element : command.getElements()) {
			if (element instanceof FrameworkArgument) {
				argumentMap.put(((FrameworkArgument<?>) element).getName(), (FrameworkArgument<?>) element);
			}
		}
		return argumentMap;
	}
	
	private void execute(CommandContext context, String[] args) {
		context.commandNode = this;
		int i = 0;
		List<FrameworkCommandElement> elements = command.getElements();
		if (command.getPermission() != null && !context.sender.hasPermission(command.getPermission())) {
			context.sender.sendMessage(Component.text("§4You have no permission to do that!").hoverEvent(
					HoverEvent.showText(Component.text("§4Missing Permission:\n§6" + command.getPermission()))));
			return;
		}
		try {
			for (FrameworkCommandElement element : elements) {
				if (element instanceof FrameworkFlag flag) {
					try {
						String current = args[i];
						if (!("-" + flag.getName()).equalsIgnoreCase(current)) {
							context.unsetFlag(flag.getName());
							continue;
						}
						context.setFlag(flag.getName());
					} catch (ArrayIndexOutOfBoundsException ignored) {
					}
				}
				else if (element instanceof FrameworkArgument<?> argument) {
					if (argument instanceof StringArrayArgument) {
						System.out.println("String Array");
						if (argument.isOptional()) {
							try {
								String[] c = Arrays.copyOfRange(args, i, args.length);
								String current = String.join(" ", c);
								context.arguments.put(argument.getName(), current);
								if (!argument.test(current)) {
									context.sender.sendMessage("$4Wrong usage: " + argument.errorMessage());
									return;
								}
							} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
							}
						}
						else {
							String[] c = Arrays.copyOfRange(args, i, args.length);
							String current = String.join(" ", c);
							context.arguments.put(argument.getName(), current);
							if (!argument.test(current)) {
								context.sender.sendMessage("§4Wrong usage: " + argument.errorMessage());
								return;
							}
						}
						i = args.length;
						break;
					}
					if (argument.isOptional()) {
						try {
							String current = args[i];
							context.arguments.put(argument.getName(), current);
							if (!argument.test(current)) {
								context.sender.sendMessage("$4Wrong usage: " + argument.errorMessage());
								return;
							}
						} catch (ArrayIndexOutOfBoundsException ignored) {
						}
					}
					else {
						String current = args[i];
						context.arguments.put(argument.getName(), current);
						if (!argument.test(current)) {
							context.sender.sendMessage("§4Wrong usage: " + argument.errorMessage());
							return;
						}
					}
				}
				i++;
			}
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
			printPaperUsage(context.sender);
			return;
		}
		if (args.length > i) {
			String current = args[i];
			if (hasChild(current)) {
				String[] nextArgs = Arrays.copyOfRange(args, i + 1, args.length);
				getChild(current).execute(context, nextArgs);
			}
			else {
				printPaperUsage(context.sender);
			}
		}
		else {
			if (command.getHandler() == null)
				printPaperUsage(context.sender);
			else
				command.getHandler().execute(context);
		}
	}
	
	private List<String> tabComplete(CommandContext context, String[] args) {
		List<String> l = new ArrayList<>();
		if (command.getPermission() != null && !context.sender.hasPermission(command.getPermission())) {
			return l;
		}
		context.commandNode = this;
		int i = 0;
		List<FrameworkCommandElement> elements = command.getElements();
		String startElement = null;
		String endElement = "";
		try {
			for (FrameworkCommandElement element : elements) {
				String current = args[i];
				if (i < args.length - 1) {
					if (element instanceof FrameworkFlag flag) {
						if (!("-" + flag.getName()).equalsIgnoreCase(current))
							continue;
						context.setFlag(flag.getName());
					}
					else if (element instanceof FrameworkArgument<?> argument) {
						context.arguments.put(argument.getName(), current);
					}
					i++;
				}
				else {
					if (startElement == null)
						startElement = element.getName();
					endElement = element.getName();
					if (element instanceof FrameworkFlag flag) {
						if (("-" + flag.getName()).toLowerCase().contains(current))
							l.add("-" + flag.getName());
					}
					else if (element instanceof FrameworkArgument<?> argument) {
						l.addAll(argument.getTabCompleteProvider().tabComplete(context, current));
						
						try {
							ActionBar.sendActionBar(context, startElement, endElement);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return l;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException ignored) {
		
		}
		String current = args[i];
		try {
			ActionBar.sendActionBar(context, startElement, endElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (args.length > i + 1) {
			if (hasChild(current)) {
				String[] nextArgs = Arrays.copyOfRange(args, i + 1, args.length);
				CommandNode child = getChild(current);
				
				l.addAll(child.tabComplete(context, nextArgs));
			}
		}
		else {
			for (CommandNode child : children.values()) {
				if (child.getCommandName().toLowerCase().startsWith(current)) {
					if (child.getCommand().getPermission() == null || context.sender.hasPermission(
							child.getCommand().getPermission()))
						l.add(child.getCommandName());
				}
			}
		}
		
		return l;
	}
	
	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		CommandContext context = new CommandContext(sender, String.join(" ", args));
		
		execute(context, args);
		return true;
	}
	
	@Override
	@NotNull
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
		CommandContext context = new CommandContext(sender, String.join(" ", args));
		
		return tabComplete(context, args);
	}
	
	public void printPaperUsage(CommandSender sender) {
		sender.sendMessage(getPaperUsage(true));
	}
	
	public TextComponent.Builder getPaperUsage(boolean last) {
		TextComponent.Builder rootComponent;
		if (parent == null) {
			rootComponent = Component.text();
			rootComponent.append(Component.text("/" + command.getName()).hoverEvent(
					HoverEvent.showText(Component.text(command.getDescription()))));
		}
		else {
			rootComponent = parent.getPaperUsage(false);
			rootComponent.append(Component.text(" "));
			rootComponent.append(Component.text(getCommandName()).hoverEvent(
					HoverEvent.showText(Component.text(command.getDescription()))));
		}
		List<FrameworkCommandElement> elements = command.getElements();
		for (FrameworkCommandElement element : elements) {
			if (element instanceof FrameworkFlag flag) {
				rootComponent.append(Component.text(" "));
				rootComponent.append(Component.text("-" + flag.getName()).hoverEvent(
						HoverEvent.showText(Component.text("[Flag] " + flag.getDescription()))));
			}
			else if (element instanceof FrameworkArgument<?> argument) {
				rootComponent.append(Component.text(" "));
				if (argument.isOptional())
					rootComponent.append(Component.text("[" + argument.getName() + "]").hoverEvent(
							HoverEvent.showText(Component.text("[Argument] " + argument.getDescription()))));
				else
					rootComponent.append(Component.text("<" + argument.getName() + ">").hoverEvent(
							HoverEvent.showText(Component.text("[Argument] " + argument.getDescription()))));
			}
		}
		if (children.size() > 0 && last) {
			rootComponent.append(Component.text(" "));
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				rootComponent.append(Component.text(child.getCommandName()).hoverEvent(
						HoverEvent.showText(Component.text(child.getCommand().getDescription()))));
				if (iterator.hasNext())
					rootComponent.append(Component.text(" | "));
			}
		}
		return rootComponent;
	}
	
	public String getStringUsage(boolean last) {
		StringBuilder sb;
		sb = new StringBuilder();
		if (parent == null) {
			sb.append("/" + command.getName());
		}
		else {
			sb.append(parent.getStringUsage(false));
			sb.append(" ");
			sb.append(getCommandName());
		}
		List<FrameworkCommandElement> elements = command.getElements();
		for (FrameworkCommandElement element : elements) {
			if (element instanceof FrameworkFlag flag) {
				sb.append(" ");
				sb.append("-" + flag.getName());
			}
			else if (element instanceof FrameworkArgument<?> argument) {
				sb.append(" ");
				if (argument.isOptional())
					sb.append("[" + argument.getName() + "]");
				else
					sb.append("<" + argument.getName() + ">");
			}
		}
		if (children.size() > 0 && last) {
			sb.append(" ");
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				sb.append(child.getCommandName());
				sb.append(" | ");
			}
		}
		return sb.toString();
	}
	
	public String getColoredStringUsage(boolean last) {
		StringBuilder sb;
		sb = new StringBuilder();
		if (parent == null) {
			sb.append(ChatColor.GRAY + "/" + ChatColor.GOLD + command.getName());
		}
		else {
			sb.append(parent.getColoredStringUsage(false));
			sb.append(" ");
			sb.append(ChatColor.AQUA + getCommandName());
		}
		List<FrameworkCommandElement> elements = command.getElements();
		for (FrameworkCommandElement element : elements) {
			if (element instanceof FrameworkFlag flag) {
				sb.append(" ");
				sb.append(ChatColor.BLUE + "-" + flag.getName());
			}
			else if (element instanceof FrameworkArgument<?> argument) {
				sb.append(" ");
				sb.append(ChatColor.DARK_PURPLE + argument.getName());
			}
		}
		if (children.size() > 0 && last) {
			sb.append(" " + ChatColor.GREEN);
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				sb.append(child.getCommandName());
				sb.append("|");
			}
		}
		return sb.toString();
	}
	
	public String getActionBarUsage(String startElement, String endElement, boolean[] editingCurrent, boolean last) {
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append(ChatColor.GRAY);
		if (parent == null) {
			sb.append("/" + command.getName());
		}
		else {
			sb.append(parent.getActionBarUsage(startElement, endElement, editingCurrent, false));
			sb.append(" " + command.getName());
		}
		List<FrameworkCommandElement> elements = command.getElements();
		for (FrameworkCommandElement element : elements) {
			if (element.getName().equals(Objects.requireNonNullElse(startElement, "")))
				editingCurrent[0] = true;
			if (element instanceof FrameworkFlag flag) {
				sb.append(" ");
				if (editingCurrent[0]) {
					sb.append(ChatColor.GREEN);
				}
				sb.append("-" + flag.getName() + "");
				sb.append(ChatColor.GRAY);
			}
			else if (element instanceof FrameworkArgument<?> argument) {
				sb.append(" ");
				if (editingCurrent[0]) {
					sb.append(ChatColor.GREEN);
				}
				if (argument.isOptional())
					sb.append("[" + argument.getName() + "]");
				else
					sb.append("<" + argument.getName() + ">");
				sb.append(ChatColor.GRAY);
			}
			if (element.getName().equals(endElement))
				editingCurrent[0] = false;
		}
		if (children.size() > 0 && last) {
			sb.append(" ");
			if (startElement == null)
				editingCurrent[0] = true;
			if (editingCurrent[0])
				sb.append(ChatColor.GREEN);
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				sb.append(child.getCommandName());
				sb.append(" | ");
			}
			sb.append(ChatColor.GRAY);
		}
		editingCurrent[0] = false;
		return sb.toString();
	}
	
}
