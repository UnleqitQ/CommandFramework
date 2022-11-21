package me.unleqitq.commandframework;

import me.unleqitq.commandframework.building.command.FrameworkCommand;
import me.unleqitq.commandframework.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandManager {
	
	public static final Map<String, CommandManager> registeredManagers = new HashMap<>();
	
	private Map<String, CommandNode> rootNodes = new HashMap<>();
	private Plugin plugin;
	
	public CommandManager(Plugin plugin) {
		this.plugin = plugin;
		registeredManagers.put(plugin.getName(), this);
	}
	
	public CommandNode register(FrameworkCommand.Builder commandBuilder) {
		CommandNode node;
		if (commandBuilder.getParent() == null) {
			if (rootNodes.containsKey(commandBuilder.getName()))
				node = rootNodes.get(commandBuilder.getName());
			else {
				node = new CommandNode(plugin, commandBuilder.build(), null);
				/*Command prev = Bukkit.getCommandMap().getCommand(commandBuilder.getName());
				if (prev != null)
					prev.unregister(Bukkit.getCommandMap());*/
				CommandUtils.getCommandMap()
						.register(node.getLabel(), plugin.getName().toLowerCase(), node);
			}
			rootNodes.put(commandBuilder.getName(), node);
		}
		else {
			CommandNode parent;
			parent = register0(commandBuilder.getParent());
			
			if (parent.hasChild(commandBuilder.getName()))
				node = parent.getChild(commandBuilder.getName());
			else node = new CommandNode(plugin, commandBuilder.build(), parent);
			parent.getChildren().put(commandBuilder.getName(), node);
		}
		updateHelp(node);
		return node;
	}
	
	public CommandNode register0(FrameworkCommand.Builder commandBuilder) {
		CommandNode node;
		if (commandBuilder.getParent() == null) {
			if (rootNodes.containsKey(commandBuilder.getName()))
				node = rootNodes.get(commandBuilder.getName());
			else {
				node = new CommandNode(plugin, commandBuilder.build(), null);
				/*Command prev = Bukkit.getCommandMap().getCommand(commandBuilder.getName());
				if (prev != null)
					prev.unregister(Bukkit.getCommandMap());*/
				CommandUtils.getCommandMap()
						.register(node.getLabel(), plugin.getName().toLowerCase(), node);
			}
			rootNodes.put(commandBuilder.getName(), node);
		}
		else {
			CommandNode parent;
			parent = register0(commandBuilder.getParent());
			
			if (parent.hasChild(commandBuilder.getName()))
				node = parent.getChild(commandBuilder.getName());
			else node = new CommandNode(plugin, commandBuilder.build(), parent);
			parent.getChildren().put(commandBuilder.getName(), node);
		}
		return node;
	}
	
	public void updateHelp(CommandNode node) {
		if (node.getParent() != null) {
			updateHelp(node.getParent());
			return;
		}
		HelpTopic topic = node.getHelpTopic();
		Bukkit.getHelpMap().addTopic(topic);
		
		if (Bukkit.getHelpMap().getHelpTopic(plugin.getName()) == null) {
			List<HelpTopic> allTopics = new ArrayList<>();
			allTopics.add(topic);
			Bukkit.getHelpMap().addTopic(
					new IndexHelpTopic(plugin.getName(), "Commands for Plugin " + plugin.getName(),
							"", allTopics) {
						@NotNull
						@Override
						public String getFullText(@NotNull CommandSender sender) {
							StringBuilder sb = new StringBuilder();
							
							if (sender instanceof ConsoleCommandSender) {
								int pageWidth = ChatPaginator.UNBOUNDED_PAGE_WIDTH;
								for (HelpTopic topic : allTopics) {
									if (topic.canSee(sender)) {
										String[] sa = ChatPaginator.wordWrap(
												topic.getFullText(sender), pageWidth);
										sb.append("§a").append(topic.getName()).append('\n');
										for (String s : sa) sb.append(s).append('\n');
										sb.append('\n');
									}
								}
								return sb.toString();
							}
							int pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 1;
							int pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;
							for (HelpTopic topic : allTopics) {
								if (topic.canSee(sender)) {
									String[] sa = ChatPaginator.wordWrap(topic.getFullText(sender),
											pageWidth);
									sb.append("§a").append(topic.getName()).append('\n');
									for (String s : sa) sb.append(s).append('\n');
									sb.append("\n".repeat((pageHeight -
														   ((sa.length + 1) % pageHeight) %
														   pageHeight)));
								}
							}
							return sb.toString();
						}
					});
		}
		else {
			IndexHelpTopic pluginTopic = ((IndexHelpTopic) Bukkit.getHelpMap()
					.getHelpTopic(plugin.getName()));
			try {
				/*System.out.println(String.join(", ",
						Arrays.stream(IndexHelpTopic.class.getDeclaredFields()).map(Field::getName)
								.toArray(String[]::new)));*/
				Field field = IndexHelpTopic.class.getDeclaredField("allTopics");
				Field chtc = GenericCommandHelpTopic.class.getDeclaredField("command");
				chtc.setAccessible(true);
				if (field == null) return;
				field.setAccessible(true);
				Collection<HelpTopic> topics = (Collection<HelpTopic>) field.get(pluginTopic);
				topics.removeIf(t -> {
					try {
						if (t instanceof GenericCommandHelpTopic tp)
							return ((Command) chtc.get(tp)).getName()
									.contentEquals(node.getCommand().getName());
					}
					catch (Exception ignored) {}
					return false;
				});
				topics.add(topic);
			}
			catch (IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public Set<CommandNode> getCommandNodes() {
		Queue<CommandNode> checkNodes = new ConcurrentLinkedQueue<>();
		Set<CommandNode> nodes = new HashSet<>();
		checkNodes.addAll(rootNodes.values());
		nodes.addAll(rootNodes.values());
		while (!checkNodes.isEmpty()) {
			CommandNode node = checkNodes.poll();
			for (CommandNode child : node.getChildren().values()) {
				checkNodes.add(child);
				nodes.add(child);
			}
		}
		return Collections.unmodifiableSet(nodes);
	}
	
	public Map<String, CommandNode> getRootNodes() {
		return Collections.unmodifiableMap(rootNodes);
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
}
