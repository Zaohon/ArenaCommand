package cn.BlockMC.Zao_Hon.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.BlockMC.Zao_Hon.Arena;
import cn.BlockMC.Zao_Hon.Message;

public class ArenaCmdCommand implements ICommand {
	private cn.BlockMC.Zao_Hon.ArenaCommand plugin;

	public ArenaCmdCommand(cn.BlockMC.Zao_Hon.ArenaCommand plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "command";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "ArenaCommand.Arena";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { Message.getString("command_description_command") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_command");
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
		if(lenth<2) {
			this.sendCommandHelp(sender);
			return true;
		}
		String arenaname = args[0];
		String argcmd = args[1];

		if(!plugin.getArenas().containsKey(arenaname)) {
			sender.sendMessage("§b该区域不存在");
			return true;
		}
		Arena arena = plugin.getArenas().get(arenaname);
		//////////////////////////////////////////
		///////// ArenaCommand command add////////
		//////////////////////////////////////////
		if (argcmd.equalsIgnoreCase("add")) {
			if (lenth == 2) {
				sender.sendMessage("§d/ArenaCommand command <区域名> add <command> xxx");
				return true;
			}
			String addcmd = "";
			for (int i = 2; i < lenth; i++) {
				addcmd = addcmd + args[i] + " ";
			}
			addcmd = addcmd.substring(0, addcmd.length() - 1);
			if(addcmd.startsWith("[leave]")) {
				addcmd = addcmd.substring(7,addcmd.length());
				String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".LeaveCommands");
				plugin.getConfig().set("Arena." + arenaname + ".LeaveCommands",
						fircmd == null ? addcmd : fircmd + ";" + addcmd);
				arena.addLeaveCommand(addcmd);
				sender.sendMessage("§b已成功向§d" + arenaname + "§b区域添加离开指令§e" + addcmd);
			}else {
				String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".Commands");
				plugin.getConfig().set("Arena." + arenaname + ".Commands",
						fircmd == null ? addcmd : fircmd + ";" + addcmd);
				arena.addCommand(addcmd);
				sender.sendMessage("§b已成功向§d" + arenaname + "§b区域添加指令§e" + addcmd);
			}
			plugin.saveConfig();
			return true;
		}
		//////////////////////////////////////////
		///////// ArenaCommand command remove//////
		//////////////////////////////////////////
		else if (argcmd.equalsIgnoreCase("remove")) {
			plugin.getConfig().set("Arena." + arenaname + ".Commands", null);
			arena.clearCommands();
			plugin.saveConfig();
			sender.sendMessage("§b已清除§d" + arenaname + "§b区域的所有指令");
			return true;
		}
		//////////////////////////////////////////
		///////// ArenaCommand command list//////
		//////////////////////////////////////////
		else if (argcmd.equalsIgnoreCase("list")) {
			sender.sendMessage("§b区域§d" + arenaname + "§b所绑定的命令有:");
			sender.sendMessage(arena.getCommandInfo());
			return true;
		}
		////////////////////////////////////////////
		//////////// ArenaCommand command round/////
		////////////////////////////////////////////
		else if (argcmd.equalsIgnoreCase("round")) {
			if (lenth != 3) {
				sender.sendMessage("§d/ArenaCommand command <区域名> round <周期时间>");
				return true;
			}
			Long round = Long.valueOf(args[2]);
			arena.setRound(round);
			sender.sendMessage("§b已设置区域§d" + arenaname + "§b指令运行周期为" + round);
			plugin.saveConfig();
			return true;
		}
		////////////////////////////////////////////
		//////////// ArenaCommand command cooltime/////
		////////////////////////////////////////////
		else if (argcmd.equalsIgnoreCase("cooltime")) {
			if (lenth != 3) {
				sender.sendMessage("§d/ArenaCommand command <区域名> cooltime <冷却时间>");
				return true;
			}
			int cooltime = Integer.valueOf(args[2]);
			arena.setCooltime(cooltime);
			sender.sendMessage("§b已设置区域§d" + arenaname + "§b指令冷却时间为为" + cooltime);
			plugin.saveConfig();
			return true;
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		int lenth = args.length;
		if(lenth ==1) {
			return List.copyOf(plugin.getArenas().keySet());
		}
		return null;
	}
	private void sendCommandHelp(CommandSender sender) {
		sender.sendMessage("§b指令管理:");
		sender.sendMessage("§b/ac command §d<区域名字> §aadd xxx xxx  §f--添加进入或离开时自动执行的指令");
		sender.sendMessage("§b/ac command §d<区域名字> §around §e<周期时间>  §f--设置在该区域内执行指令的周期");
		sender.sendMessage("§b/ac command §d<区域名字> §acooltime §e<冷却时间>  §f--设置进出该区域执行指令的冷却时间");
		sender.sendMessage("§b/ac command §d<区域名字> §aremove  §f--移除该区域的所有指令");
		sender.sendMessage("§b/ac command §d<区域名字> §alist  §f--列出该区域所绑定的指令");
	}
}
