package cn.BlockMC.Zao_Hon.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import cn.BlockMC.Zao_Hon.Arena;
import cn.BlockMC.Zao_Hon.Message;

public class ArenaCommand implements ICommand {
	private cn.BlockMC.Zao_Hon.ArenaCommand plugin;

	public ArenaCommand(cn.BlockMC.Zao_Hon.ArenaCommand plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "arena";
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
		return new String[] { Message.getString("command_description_arena") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_arena");
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
		if(lenth==0) {
			this.sendArenaHelp(sender);
			return true;
		}
		
		Player p = (Player) sender;
		if (args[0].equalsIgnoreCase("list")) {
			p.sendMessage("§b------区域列表------");
			if (plugin.getArenas().isEmpty()) {
				p.sendMessage("§c        无");
				p.sendMessage("§c       NOPE");
			} else {
				plugin.getArenas().keySet().forEach(name ->p.sendMessage(name));
			}
			p.sendMessage("§b---------------------");
			return true;
		}
		//////////////////////////////////////////////
		if (lenth != 2) {
			this.sendArenaHelp(p);
			return true;
		}
		String arenaname = args[0];
		String argcmd = args[1];
		boolean exist = plugin.getArenas().containsKey(arenaname);
		/////////////////////////////////////////
		//////// ArenaCommand arena create////////
		/////////////////////////////////////////
		if (argcmd.equalsIgnoreCase("create")) {
			if (exist) {
				p.sendMessage("§b这个区域已经存在了");
				return true;
			}
			
			p.sendMessage("§d开始创建"+arenaname);
			p.sendMessage("§b请右键设置所创区域的左下角");
			plugin.getServer().getPluginManager().registerEvents(new CreateArenaLister(p.getUniqueId(),arenaname), plugin);
			
			/*
			
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
			
			
			*/
			
			
		}
		/////////////////////////////////////////
		//////// ArenaCommand arena remove///////
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
		///////// ArenaCommand arena display//////////
		//////////////////////////////////////////
		else if (argcmd.equalsIgnoreCase("display")) {
			
			if (!exist) {
				p.sendMessage("§b区域不存在");
				return true;
			}
			Arena arena = plugin.getArenas().get(arenaname);
			arena.displayToPlayer(p);
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
	
	private void sendArenaHelp(CommandSender sender) {
		sender.sendMessage("§b---------------ArenaCommand-----------------");
		sender.sendMessage("§b区域管理:");
		sender.sendMessage("§e/ac arena list §f--查看所有已创建好的区域");
		sender.sendMessage("§b/ac arena §d<区域名字> §acreate  §f--创建一个区域");
		sender.sendMessage("§b/ac arena §d<区域名字> §aremove  §f--删除一个区域");
		sender.sendMessage("§b/ac arena §d<区域名字> §ainfo  §f--查看某区域的信息");
		sender.sendMessage("§b/ac arena §d<区域名字> §adisplay  §f--查看某区域的信息");
		sender.sendMessage("§b/ac arena §d<区域名字> §atp  §f--传送至该区域");
		sender.sendMessage("§b-------------------------------------");
	}
	
	
	class CreateArenaLister implements Listener{
		private UUID uuid;
		private String name;
		
		private Location loc1;
		private Location loc2;
		
		
		CreateArenaLister(UUID uuid,String arenaName){
			this.uuid = uuid;
			this.name = arenaName;
			plugin.getServer().getScheduler().runTaskLater(plugin, ()->
				HandlerList.unregisterAll(this)
			, 20*20*30);
		}
		
		@EventHandler
		public void onPlayerInteractBlock(PlayerInteractEvent event) {
			Player p = event.getPlayer();
			if (!p.getUniqueId().equals(uuid) || event.getClickedBlock() == null
					|| event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			event.setCancelled(true);	
			Location loc = event.getClickedBlock().getLocation();
			if(loc.equals(loc1)) {
				return;
			}
			if(loc1==null) {
				loc1 = loc;
				p.sendMessage("§b请再右键设置所创区域的右下角");
			}else {
				loc2 = loc;
				Arena arena = new Arena(name, loc1.getWorld(), loc1, loc2);
				arena.saveToConfig(plugin);
				plugin.getArenas().put(name, arena);
				p.sendMessage("§b区域§d" + name + "§b已创建完成");
				HandlerList.unregisterAll(this);
			}
		} 
		
	}

}
