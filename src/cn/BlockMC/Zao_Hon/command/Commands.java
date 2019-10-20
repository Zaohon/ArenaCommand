package cn.BlockMC.Zao_Hon.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import cn.BlockMC.Zao_Hon.Arena;
import cn.BlockMC.Zao_Hon.ArenaCommand;

public class Commands implements CommandExecutor {
	private ArenaCommand plugin;

	public Commands(ArenaCommand plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ArenaCommand")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§c这个指令只有玩家才能使用");
				return true;
			}
			Player p = (Player) sender;
			int lenth = args.length;
			if (lenth == 0) {
				sendPluginHelp(p);
				return true;
			}
			/////////////////////////////////////////////
			// 区域管理指令
			if (args[0].equalsIgnoreCase("arena")) {
				if (!p.hasPermission("ArenaCommand.Arena")) {
					p.sendMessage("§c权限不足");
					return true;
				}
				//////////////////////////////////////////
				//////// ArenaCommand arena list///////////
				//////////////////////////////////////////
				if (lenth == 2 && args[1].equalsIgnoreCase("list")) {
					p.sendMessage("§b------区域列表------");
					if (plugin.getArenas().isEmpty()) {
						p.sendMessage("§c        无");
						p.sendMessage("§c       NOPE");
					} else {
						plugin.getArenas().keySet().forEach(name -> {
							p.sendMessage(name);
						});

					}
					p.sendMessage("§b---------------------");
					return true;
				}
				//////////////////////////////////////////////
				if (lenth != 3) {
					this.sendArenaHelp(p);
					return true;
				}
				String arenaname = args[1];
				String argcmd = args[2];
				boolean exist = plugin.getArenas().containsKey(arenaname);
				/////////////////////////////////////////
				//////// ArenaCommand arena create////////
				/////////////////////////////////////////
				if (argcmd.equalsIgnoreCase("create")) {
					if (exist) {
						p.sendMessage("§b这个区域已经存在了");
						return true;
					}
					p.sendMessage("§d请右键设置所需创建区域的左下角");
					Listener lis = new Listener() {
						Location loc1;

						@EventHandler
						public void click(PlayerInteractEvent e) {
							if (!e.getPlayer().equals(p) || e.getClickedBlock() == null
									|| e.getAction() != Action.RIGHT_CLICK_BLOCK) {
								return;
							}
							loc1 = e.getClickedBlock().getLocation();
							p.sendMessage("§b第一个点设置完毕:§d" + loc1.getX() + "," + loc1.getY() + "," + loc1.getZ() + "");
							e.setCancelled(true);
							HandlerList.unregisterAll(this);
							p.sendMessage("§b请右键点击区域的右上角");
							Listener lis = new Listener() {
								Location loc2;

								@EventHandler
								public void click(PlayerInteractEvent e) {
									loc2 = e.getClickedBlock().getLocation();
									if (!e.getPlayer().equals(p) || e.getClickedBlock() == null
											|| e.getAction() != Action.RIGHT_CLICK_BLOCK || loc1.equals(loc2)) {
										return;
									}

									p.sendMessage("§b第二个点设置完成(§d" + loc2.getBlockX() + "§b,§d" + loc2.getBlockY()
											+ "§b,§d" + loc2.getBlockZ() + "§b)");
									e.setCancelled(true);
									HandlerList.unregisterAll(this);
									Arena arena = new Arena(arenaname, loc1.getWorld(), loc1, loc2);
									arena.saveToConfig(plugin);
									plugin.getArenas().put(arenaname, arena);
									p.sendMessage("§b区域§d" + arenaname + "§b已创建完成");
									return;
								}
							};
							plugin.getServer().getPluginManager().registerEvents(lis, plugin);
						}
					};
					plugin.getServer().getPluginManager().registerEvents(lis, plugin);
				}
				/////////////////////////////////////////
				//////// ArenaCommand arena remove////////
				/////////////////////////////////////////
				else if (argcmd.equalsIgnoreCase("remove")) {
					if (exist) {
						plugin.getArenas().remove(arenaname);
						plugin.getConfig().set("Arena." + arenaname, null);
						p.sendMessage("§b区域已删除");
					} else {
						p.sendMessage("§b区域不存在");
					}
					return true;
				}
				//////////////////////////////////////////
				///////// ArenaCommand arena tp//////////
				//////////////////////////////////////////
				else if (argcmd.equalsIgnoreCase("tp")) {
					if (exist) {
						p.teleport(plugin.getArenas().get(arenaname).getLocationLow());
					} else {
						p.sendMessage("§b区域不存在");
					}
					return true;

				}
				//////////////////////////////////////////
				///////// ArenaCommand arena info//////////
				//////////////////////////////////////////
				else if (argcmd.equalsIgnoreCase("info")) {
					if (plugin.getArenas().containsKey(arenaname)) {
						Arena arena = plugin.getArenas().get(arenaname);
						p.sendMessage(arena.returninfo());
					} else {
						p.sendMessage("§b区域不存在");
					}
					return true;
				}
				//////////////////////////////////
				//////////////////////////////////
				else {
					this.sendArenaHelp(p);
					return true;
				}
			}
			//////////////////////////////////////////////
			// 区域绑定指令相关
			else if (args[0].equalsIgnoreCase("command")) {
				if (!p.hasPermission("ArenaCommand.Command")) {
					p.sendMessage("§c权限不足");
					return true;
				}
				if (lenth < 3) {
					this.sendCommandHelp(p);
					return true;
				}
				String arenaname = args[1];
				String argcmd = args[2];

				if (plugin.getArenas().containsKey(arenaname)) {
					Arena arena = plugin.getArenas().get(arenaname);
					//////////////////////////////////////////
					///////// ArenaCommand command add////////
					//////////////////////////////////////////
					if (argcmd.equalsIgnoreCase("add")) {
						if (lenth == 3) {
							p.sendMessage("§d/ArenaCommand command <区域名> add <command> xxx");
							return true;
						}
						String addcmd = "";
						for (int i = 3; i < lenth; i++) {
							addcmd = addcmd + args[i] + " ";
						}
						addcmd = addcmd.substring(0, addcmd.length() - 1);
						if(addcmd.startsWith("[leave]")) {
							addcmd = addcmd.substring(7,addcmd.length());
							String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".LeaveCommands");
							plugin.getConfig().set("Arena." + arenaname + ".LeaveCommands",
									fircmd == null ? addcmd : fircmd + ";" + addcmd);
							arena.addLeaveCommand(addcmd);
							p.sendMessage("§b已成功向§d" + arenaname + "§b区域添加离开指令§e" + addcmd);
						}else {
							String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".Commands");
							plugin.getConfig().set("Arena." + arenaname + ".Commands",
									fircmd == null ? addcmd : fircmd + ";" + addcmd);
							arena.addCommand(addcmd);
							p.sendMessage("§b已成功向§d" + arenaname + "§b区域添加指令§e" + addcmd);
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
						p.sendMessage("§b已清除§d" + arenaname + "§b区域的所有指令");
						return true;
					}
					//////////////////////////////////////////
					///////// ArenaCommand command list//////
					//////////////////////////////////////////
					else if (argcmd.equalsIgnoreCase("list")) {
						p.sendMessage("§b区域§d" + arenaname + "§b所绑定的命令有:");
						p.sendMessage(arena.getCommandInfo());
						return true;
					}
					////////////////////////////////////////////
					//////////// ArenaCommand command round/////
					////////////////////////////////////////////
					else if (argcmd.equalsIgnoreCase("round")) {
						if (lenth == 3) {
							p.sendMessage("§d/ArenaCommand command <区域名> round <周期时间>");
							return true;
						}
						Long round = Long.valueOf(args[3]);
						arena.setRound(round);
						p.sendMessage("§b已设置区域§d" + arenaname + "§b指令运行周期为" + round);
						return true;
					}
					///////////////////////////////
					///////////////////////////////
					else {
						this.sendCommandHelp(p);
						return true;
					}
				} else {
					p.sendMessage("§b该区域不存在");
					return true;
				}
			}
			//////////////////////////////////////////////////////////
			////// 插件管理命令
			else if (args[0].equalsIgnoreCase("admin")) {
				if (!p.hasPermission("ArenaCommand.Admin")) {
					p.sendMessage("§c权限不足");
					return true;
				}
				if (lenth != 2) {
					this.sendAdminHelp(p);
					return true;
				}
				String argcmd = args[1];
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
					p.sendMessage("§b[§b§l区域指令§b]§a插件重载完成");
					return true;
				}
				////////////////////////////////////////////
				else {
					this.sendAdminHelp(p);
					return true;
				}
			}
			/////////////////////////////////////////////
			else {
				this.sendPluginHelp(p);
				return true;
			}
		}
		return false;
	}

	private void sendPluginHelp(Player p) {
		p.sendMessage("§b---------------ArenaCommand-----------------");
		p.sendMessage("§b插件帮助:");
		p.sendMessage("§e/ac arena   --查看区域指令帮助");
		p.sendMessage("§e/ac command --查看指令相关帮助");
		p.sendMessage("§e/ac admin   --查看插件管理帮助");
		p.sendMessage("§b--------------------------------------");
	}

	private void sendArenaHelp(Player p) {
		p.sendMessage("§b---------------ArenaCommand-----------------");
		p.sendMessage("§b区域管理:");
		p.sendMessage("§e/ac arena list §f--查看所有已创建好的区域");
		p.sendMessage("§b/ac arena §d<区域名字> §acreate  §f--创建一个区域");
		p.sendMessage("§b/ac arena §d<区域名字> §aremove  §f--删除一个区域");
		p.sendMessage("§b/ac arena §d<区域名字> §ainfo  §f--查看某区域的信息");
		p.sendMessage("§b/ac arena §d<区域名字> §atp  §f--传送至该区域");
		p.sendMessage("§b-------------------------------------");
	}

	private void sendCommandHelp(Player p) {
		p.sendMessage("§b指令管理:");
		p.sendMessage("§b/ac command §d<区域名字> §aadd xxx xxx  §f--添加进入或离开时自动执行的指令");
		p.sendMessage("§b/ac command §d<区域名字> §around §e<周期时间>  §f--设置该区域进入时运行指令的周期");
		p.sendMessage("§b/ac command §d<区域名字> §aremove  §f--移除该区域的所有指令");
		p.sendMessage("§b/ac command §d<区域名字> §alist  §f--列出该区域所绑定的指令");
	}

	private void sendAdminHelp(Player p) {
		p.sendMessage("§b插件管理指令");
		p.sendMessage("§e/ac admin reload §f--重载插件");
	}

}
