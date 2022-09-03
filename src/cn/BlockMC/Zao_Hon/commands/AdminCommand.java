package cn.BlockMC.Zao_Hon.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import cn.BlockMC.Zao_Hon.Arena;
import cn.BlockMC.Zao_Hon.Message;

public class AdminCommand implements ICommand {
	private cn.BlockMC.Zao_Hon.ArenaCommand plugin;

	public AdminCommand(cn.BlockMC.Zao_Hon.ArenaCommand plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "admin";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "ArenaCommand.Admin";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { Message.getString("command_description_admin") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_admin");
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
		int lenth = args.length;
		if (lenth != 1) {
			this.sendAdminHelp(sender);
			return true;
		}
		String argcmd = args[0];
		////////////////////////////////////////////
		///////// ArenaCommand admin reload//////////
		////////////////////////////////////////////
		if (argcmd.equalsIgnoreCase("reload")) {
			plugin.reloadConfig();
			plugin.getArenas().values().forEach(arena->{
				arena.destory();
			});
			plugin.getArenas().clear();
			plugin.loadArena();
			sender.sendMessage("§b[§b§l区域指令§b]§a插件重载完成");
			return true;
		}
		////////////////////////////////////////////
		else {
			this.sendAdminHelp(sender);
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
//		int lenth = args.length;
//		if(lenth ==0) {
//			return List.copyOf(plugin.getArenas().keySet());
//		}
		return null;

	}
	
	private void sendAdminHelp(CommandSender p) {
		p.sendMessage("§b插件管理指令");
		p.sendMessage("§e/ac admin reload §f--重载插件");
	}

}
