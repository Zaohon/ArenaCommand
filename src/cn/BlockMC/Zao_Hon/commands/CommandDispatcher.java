package cn.BlockMC.Zao_Hon.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import cn.BlockMC.Zao_Hon.ArenaCommand;
import cn.BlockMC.Zao_Hon.Message;


public class CommandDispatcher implements CommandExecutor, TabCompleter {
	private ArenaCommand plugin;

	private LinkedHashMap<String, ICommand> mCommands;
	private String mRootCommandName;

	private String mRootCommandDescription;

	public CommandDispatcher(ArenaCommand plugin, String commandName, String description) {
		this.plugin = plugin;

		mRootCommandName = commandName;
		mRootCommandDescription = description;

		mCommands = new LinkedHashMap<>();

		registerCommand(new InternalHelp());
	}

	/**
	 * Registers a command to be handled by this dispatcher
	 *
	 * @param command
	 */
	public void registerCommand(ICommand command) {
		mCommands.put(command.getName().toLowerCase(), command);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			List<String> list = new ArrayList<String>();
			mCommands.keySet().forEach(key->list.add(key));
			return list;
		}
		String subCommand = args[0].toLowerCase();
		String[] subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);

		ICommand com = isSubCommand(subCommand);
		if (com == null) {
			return null;
		}

		return com.onTabComplete(sender, label, subArgs);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			displayUsage(sender, label, null);
			return true;
		}

		String subCommand = args[0].toLowerCase();
		String[] subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);

		ICommand com = isSubCommand(subCommand);

		// Was not found
		if (com == null) {
			displayUsage(sender, label, subCommand);
			return true;
		}

		// Check that the sender is correct
		if (!com.canBeConsole()
				&& (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender)) {
			Message.senderSendMessage(sender, Message.getString("command_error_player_only"));
			return true;
		}
		if (!com.canBeCommandBlock() && sender instanceof BlockCommandSender) {
			Message.senderSendMessage(sender, Message.getString("command_error_player_only"));
			return true;
		}

		// Check that they have permission
		if (com.getPermission() != null && !sender.hasPermission(com.getPermission())) {
			Message.senderSendMessage(sender, Message.getString("command_no_permission"));
			return true;
		}

		if (!com.onCommand(sender, subCommand, subArgs)) {
			String[] lines = com.getUsageString(subCommand, sender);
			Message.senderSendMessage(sender, Message.getString("command_error_grammar"));
			sender.sendMessage(lines);
		}

		return true;

	}

	public ICommand isSubCommand(String subCommand) {
		if (mCommands.containsKey(subCommand)) {
			return mCommands.get(subCommand);
		} else {
			// Check aliases
			for (Entry<String, ICommand> ent : mCommands.entrySet()) {
				if (ent.getValue().getAliases() != null) {
					String[] aliases = ent.getValue().getAliases();
					for (String alias : aliases) {
						if (subCommand.equalsIgnoreCase(alias)) {
							return ent.getValue();
						}
					}
				}
			}
		}
		return null;
	}

	private void displayUsage(CommandSender sender, String label, String subcommand) {

		if (subcommand != null) {
			Message.senderSendMessage(sender, Message.getString("command_heading"));
			Message.senderSendMessage(sender, Message.getString("command_error_command") + ":" + subcommand);
			for (ICommand command : mCommands.values()) {
				sender.sendMessage(command.getDescription());
			}
		} else {
			Message.senderSendMessage(sender, Message.getString("command_heading"));
			Message.senderSendMessage(sender, Message.getString("command_error_invalid"));
			for (ICommand command : mCommands.values()) {
				sender.sendMessage(command.getDescription());
			}
		}

	}

	private class InternalHelp implements ICommand {

		@Override
		public String getName() {
			return "help";
		}

		@Override
		public String[] getAliases() {
			return null;
		}

		@Override
		public String getPermission() {
			return null;
		}

		@Override
		public String[] getUsageString(String label, CommandSender sender) {
			return new String[] { Message.getString("command_description_help") };
		}

		@Override
		public String getDescription() {
			return Message.getString("command_description_help");
		}

		@Override
		public boolean canBeConsole() {
			return true;
		}

		@Override
		public boolean canBeCommandBlock() {
			return true;
		}

		@Override
		public boolean onCommand(CommandSender sender, String label, String[] args) {
			if (args.length != 0)
				return false;

			Message.senderSendMessage(sender, Message.getString("command_heading"));
			for (ICommand command : mCommands.values()) {
				// Dont show commands that are irrelevant
				if (!command.canBeCommandBlock() && sender instanceof BlockCommandSender)
					continue;
				if (!command.canBeConsole()
						&& (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender))
					continue;

				if (command.getPermission() != null && !sender.hasPermission(command.getPermission()))
					continue;
				Message.senderSendMessage(sender, command.getDescription());
			}
			return true;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
			return null;
		}

	}

}
