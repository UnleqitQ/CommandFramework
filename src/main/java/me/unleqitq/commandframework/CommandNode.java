package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.FrameworkCommandElement;
import me.unleqitq.commandframework.building.argument.FrameworkArgument;
import me.unleqitq.commandframework.building.argument.StringArrayArgument;
import me.unleqitq.commandframework.building.command.FrameworkCommand;
import me.unleqitq.commandframework.building.flag.FrameworkFlag;
import me.unleqitq.commandframework.utils.CommandUtils;
import me.unleqitq.commandframework.utils.ComponentWrapper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandNode extends Command implements PluginIdentifiableCommand {
	
	private ConcurrentMap<UUID, Integer> useMap = new ConcurrentHashMap<>();
	
	@NotNull
	private FrameworkCommand command;
	
	private Map<String, CommandNode> children = new HashMap<>();
	@Nullable
	private CommandNode parent;
	
	@NotNull
	private Plugin plugin;
	
	public CommandNode(Plugin plugin, FrameworkCommand command, @Nullable CommandNode parent) {
		super(command.getName());
		this.command = command;
		this.parent = parent;
		this.plugin = plugin;
		
		setAliases(Arrays.stream(command.getAliases()).toList());
		setUsage(getStringUsage(true));
		setDescription(command.getDescription());
		setPermission(command.getPermission());
	}
	
	public HelpTopic getHelpTopic() {
		StringBuilder sb = new StringBuilder();
		sb.append("§6Description: §r");
		sb.append(command.getDescription());
		sb.append('\n');
		sb.append("§6Usage: §r");
		sb.append(getStringUsage(true));
		sb.append('\n');
		sb.append("§6Aliases: §r");
		sb.append(getAliases().size() == 0 ? "§4----" : String.join(", ", getAliases()));
		for (CommandNode child : children.values()) {
			sb.append('\n');
			sb.append("§6");
			sb.append(child.getCommandName());
			sb.append("(§r");
			sb.append(child.getStringUsage(true));
			sb.append("§6): §r");
			sb.append(child.command.getDescription());
		}
		HelpTopic h = new GenericCommandHelpTopic(this);
		h.amendTopic(command.getDescription(), sb.toString());
		return h;
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
		if (children.containsKey(name)) return children.get(name);
		return children.values().stream()
				.sorted((s1, s2) -> String.CASE_INSENSITIVE_ORDER.compare(s1.getCommandName(),
						s2.getCommandName())).filter(n -> n.getName().equalsIgnoreCase(name) ||
														  n.getAliases().stream().anyMatch(
																  s -> s.equalsIgnoreCase(name)))
				.findFirst().orElse(null);
	}
	
	public boolean hasChild(String name) {
		if (children.containsKey(name)) return true;
		return children.values().stream().anyMatch(n -> n.getName().equalsIgnoreCase(name) ||
														n.getAliases().stream().anyMatch(
																s -> s.equalsIgnoreCase(name)));
	}
	
	public Map<String, FrameworkArgument<?>> allArguments() {
		Map<String, FrameworkArgument<?>> argumentMap = new HashMap<>();
		if (parent != null) {
			argumentMap.putAll(parent.allArguments());
		}
		for (Object element : command.getElements()) {
			if (element instanceof FrameworkArgument) {
				argumentMap.put(((FrameworkArgument<?>) element).getName(),
						(FrameworkArgument<?>) element);
			}
		}
		return argumentMap;
	}
	
	public boolean execute(CommandContext context, String[] args) {
		try {
			context.commandNode = this;
			int i = 0;
			List<FrameworkCommandElement> elements = command.getElements();
			if (command.getPermission() != null &&
				!context.sender.hasPermission(command.getPermission())) {
				CommandUtils.printMissingPermission(context.getSender(), command.getPermission());
				return false;
			}
			if (!command.getSenderClass().isAssignableFrom(context.sender.getClass())) {
				context.sender.sendMessage("§4You can only excute this command as " +
										   command.getSenderClass().getSimpleName());
				return false;
			}
			if (context.getSender() instanceof Player sender && command.getCooldown() > 0) {
				if (!sender.hasPermission(command.getCooldownBypassPermission()) &&
					sender.getStatistic(Statistic.PLAY_ONE_MINUTE) -
					useMap.getOrDefault(sender.getUniqueId(), 0) < command.getCooldown()) {
					int sec = (command.getCooldown() -
							   sender.getStatistic(Statistic.PLAY_ONE_MINUTE) +
							   useMap.getOrDefault(sender.getUniqueId(), 0)) / 20;
					int min = sec / 60;
					int hour = min / 60;
					String s = "";
					if (hour > 0) s += hour + "h ";
					if (min % 60 > 0) s += min % 60 + "m ";
					if (sec == 0 || sec % 60 > 0) s += sec % 60 + "s ";
					sender.sendMessage(String.format(command.getCooldownMessage(), s));
					return false;
				}
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
						}
						catch (ArrayIndexOutOfBoundsException ignored) {
						}
					}
					else if (element instanceof FrameworkArgument<?> argument) {
						if (argument instanceof StringArrayArgument) {
							if (argument.isOptional()) {
								try {
									String[] c = Arrays.copyOfRange(args, i, args.length);
									String current = String.join(" ", c);
									context.arguments.put(argument.getName(), current);
									if (!argument.test(context, current)) {
										context.sender.sendMessage(
												"§4Wrong usage: " + argument.errorMessage());
										return false;
									}
								}
								catch (ArrayIndexOutOfBoundsException |
									   IllegalArgumentException ignored) {
								}
							}
							else {
								String[] c = Arrays.copyOfRange(args, i, args.length);
								String current = String.join(" ", c);
								context.arguments.put(argument.getName(), current);
								if (!argument.test(context, current)) {
									context.sender.sendMessage(
											"§4Wrong usage: " + argument.errorMessage());
									return false;
								}
							}
							i = args.length;
							break;
						}
						if (argument.isOptional()) {
							try {
								String current = args[i];
								context.arguments.put(argument.getName(), current);
								if (!argument.test(context, current)) {
									context.sender.sendMessage(
											"§4Wrong usage: " + argument.errorMessage());
									return false;
								}
							}
							catch (ArrayIndexOutOfBoundsException ignored) {
							}
						}
						else {
							String current = args[i];
							context.arguments.put(argument.getName(), current);
							if (!argument.test(context, current)) {
								context.sender.sendMessage(
										"§4Wrong usage: " + argument.errorMessage());
								return false;
							}
						}
					}
					i++;
				}
			}
			catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
				printComponentUsage(context.sender);
				return false;
			}
			if (args.length > i) {
				String current = args[i];
				if (hasChild(current)) {
					String[] nextArgs = Arrays.copyOfRange(args, i + 1, args.length);
					boolean flag = getChild(current).execute(context, nextArgs);
					if (flag && command.getCooldown() > 0 &&
						context.getSender() instanceof Player sender &&
						!sender.hasPermission(command.getCooldownBypassPermission()))
						useMap.put(sender.getUniqueId(),
								sender.getStatistic(Statistic.PLAY_ONE_MINUTE));
					return flag;
				}
				else {
					printComponentUsage(context.sender);
				}
			}
			else {
				if (command.getHandler() == null) printComponentUsage(context.sender);
				else {
					boolean flag = command.getHandler().execute(context);
					if (flag && command.getCooldown() > 0 &&
						context.getSender() instanceof Player sender &&
						!sender.hasPermission(command.getCooldownBypassPermission()))
						useMap.put(sender.getUniqueId(),
								sender.getStatistic(Statistic.PLAY_ONE_MINUTE));
					return flag;
				}
			}
		}
		catch (Exception e) {
			context.sender.sendMessage("§4Some Error occured: " + e.getMessage());
			//Bukkit.getLogger().log(Level.INFO, e.getMessage(), e);
			//context.sender.sendMessage("§4" + e.getMessage());
		}
		return false;
	}
	
	public void executeIgnorePerms(CommandContext context, String[] args) {
		try {
			Bukkit.getLogger()
					.info("Executed \"" + command.getName() + " " + String.join(" ", args) +
						  "\"\nwith " + context);
			context.commandNode = this;
			int i = 0;
			List<FrameworkCommandElement> elements = command.getElements();
			if (!command.getSenderClass().isAssignableFrom(context.sender.getClass())) {
				context.sender.sendMessage("§4You can only excute this command as " +
										   command.getSenderClass().getSimpleName());
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
						}
						catch (ArrayIndexOutOfBoundsException ignored) {
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
									if (!argument.test(context, current)) {
										context.sender.sendMessage(
												"§4Wrong usage: " + argument.errorMessage());
										return;
									}
								}
								catch (ArrayIndexOutOfBoundsException |
									   IllegalArgumentException ignored) {
								}
							}
							else {
								String[] c = Arrays.copyOfRange(args, i, args.length);
								String current = String.join(" ", c);
								context.arguments.put(argument.getName(), current);
								if (!argument.test(context, current)) {
									context.sender.sendMessage(
											"§4Wrong usage: " + argument.errorMessage());
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
								if (!argument.test(context, current)) {
									context.sender.sendMessage(
											"§4Wrong usage: " + argument.errorMessage());
									return;
								}
							}
							catch (ArrayIndexOutOfBoundsException ignored) {
							}
						}
						else {
							String current = args[i];
							context.arguments.put(argument.getName(), current);
							if (!argument.test(context, current)) {
								context.sender.sendMessage(
										"§4Wrong usage: " + argument.errorMessage());
								return;
							}
						}
					}
					i++;
				}
			}
			catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
				printComponentUsage(context.sender);
				return;
			}
			if (args.length > i) {
				String current = args[i];
				if (hasChild(current)) {
					String[] nextArgs = Arrays.copyOfRange(args, i + 1, args.length);
					getChild(current).executeIgnorePerms(context, nextArgs);
				}
				else {
					printComponentUsage(context.sender);
				}
			}
			else {
				if (command.getHandler() == null) printComponentUsage(context.sender);
				else command.getHandler().execute(context);
			}
		}
		catch (Exception e) {
			context.sender.sendMessage("§4Some Error occured: " + e.getMessage());
			//context.sender.sendMessage("§4" + e.getMessage());
		}
	}
	
	private List<String> tabComplete(CommandContext context, String[] args) {
		try {
			List<String> l = new ArrayList<>();
			if (command.getPermission() != null &&
				!context.sender.hasPermission(command.getPermission())) {
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
							if (!("-" + flag.getName()).equalsIgnoreCase(current)) continue;
							context.setFlag(flag.getName());
						}
						else if (element instanceof FrameworkArgument<?> argument) {
							context.arguments.put(argument.getName(), current);
						}
						i++;
					}
					else {
						if (startElement == null) startElement = element.getName();
						endElement = element.getName();
						if (element instanceof FrameworkFlag flag) {
							if (("-" + flag.getName()).toLowerCase().contains(current))
								l.add("-" + flag.getName());
						}
						else if (element instanceof FrameworkArgument<?> argument) {
							l.addAll(argument.getTabCompleteProvider()
									.tabComplete(context, current));
							
							try {
								ActionBar.sendActionBar(context, startElement, endElement);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							return l;
						}
					}
				}
			}
			catch (ArrayIndexOutOfBoundsException ignored) {
			
			}
			String current = args[i];
			try {
				ActionBar.sendActionBar(context, startElement, endElement);
			}
			catch (Exception e) {
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
						if (child.getCommand().getPermission() == null ||
							context.sender.hasPermission(child.getCommand().getPermission()))
							l.add(child.getCommandName());
					}
				}
			}
			
			return l;
		}
		catch (Exception e) {
			return List.of("Housten we got a Problem");
		}
	}
	
	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel,
						   @NotNull String[] args) {
		CommandContext context = new CommandContext(sender, String.join(" ", args));
		
		execute(context, args);
		return true;
	}
	
	@Override
	@NotNull
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
									@NotNull String[] args) {
		CommandContext context = new CommandContext(sender, String.join(" ", args));
		
		return tabComplete(context, args);
	}
	
	public void printComponentUsage(CommandSender sender) {
		sender.spigot().sendMessage(getComponentUsage(true).create());
	}
	
	public ComponentBuilder getComponentUsage(boolean last) {
		ComponentBuilder rootComponent;
		
		if (parent == null) {
			rootComponent = new ComponentBuilder();
			rootComponent.append(
					new ComponentWrapper(new TextComponent("§a/" + command.getName())).hoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new Text(command.getDescription()))).component());
		}
		else {
			rootComponent = parent.getComponentUsage(false);
			rootComponent.append(new TextComponent(" "));
			rootComponent.append(
					new ComponentWrapper(new TextComponent(getCommandName())).hoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new Text(command.getDescription()))).component());
		}
		List<FrameworkCommandElement> elements = command.getElements();
		for (FrameworkCommandElement element : elements) {
			if (element instanceof FrameworkFlag flag) {
				rootComponent.append(new TextComponent(" "));
				rootComponent.append(
						new ComponentWrapper(new TextComponent("§e-" + flag.getName())).hoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new Text("§b[Flag] §f" + flag.getDescription()))).component());
			}
			else if (element instanceof FrameworkArgument<?> argument) {
				rootComponent.append(new TextComponent(" "));
				if (argument.isOptional()) rootComponent.append(new ComponentWrapper(
						new TextComponent("§b[" + argument.getName() + "]")).hoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
								String.format("§b[Argument §6%s§b] §f%s", argument.argumentType(),
										argument.getDescription())))).component());
				else rootComponent.append(new ComponentWrapper(
						new TextComponent("§b<" + argument.getName() + ">")).hoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
								String.format("§b[Argument §6%s§b] §f%s", argument.argumentType(),
										argument.getDescription())))).component());
			}
		}
		if (children.size() > 0 && last) {
			rootComponent.append(new TextComponent(" "));
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				String s = "/cfhelp " + plugin.getName();
				String s0 = "";
				CommandNode n = child;
				do {
					s0 = " " + n.command.getName() + s0;
					n = n.parent;
				} while (n != null);
				rootComponent.append(
						new ComponentWrapper(new TextComponent(child.getCommandName())).hoverEvent(
										new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new Text(child.getCommand().getDescription())))
								.clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, s + s0))
								.component());
				if (iterator.hasNext()) rootComponent.append(new TextComponent(" | "));
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
				if (argument.isOptional()) sb.append("[" + argument.getName() + "]");
				else sb.append("<" + argument.getName() + ">");
			}
		}
		if (children.size() > 0 && last) {
			sb.append(" ");
			for (CommandNode child : children.values()) {
				sb.append(child.getCommandName());
				sb.append(" | ");
			}
			/*Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				sb.append(child.getCommandName());
				sb.append(" | ");
			}*/
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
	
	public String getActionBarUsage(String startElement, String endElement,
									boolean[] editingCurrent, boolean last) {
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
				if (argument.isOptional()) sb.append("[" + argument.getName() + "]");
				else sb.append("<" + argument.getName() + ">");
				sb.append(ChatColor.GRAY);
			}
			if (element.getName().equals(endElement)) editingCurrent[0] = false;
		}
		if (children.size() > 0 && last) {
			sb.append(" ");
			if (startElement == null) editingCurrent[0] = true;
			if (editingCurrent[0]) sb.append(ChatColor.GREEN);
			Iterator<CommandNode> iterator = children.values().iterator();
			while (iterator.hasNext()) {
				CommandNode child = iterator.next();
				sb.append(child.getCommandName());
				if (iterator.hasNext()) sb.append(" | ");
			}
			sb.append(ChatColor.GRAY);
		}
		editingCurrent[0] = false;
		return sb.toString();
	}
	
	@Override
	@NotNull
	public Plugin getPlugin() {
		return plugin;
	}
	
}
