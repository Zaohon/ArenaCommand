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
				sender.sendMessage("��c���ָ��ֻ����Ҳ���ʹ��");
				return true;
			}
			Player p = (Player) sender;
			int lenth = args.length;
			if (lenth == 0) {
				sendPluginHelp(p);
				return true;
			}
			/////////////////////////////////////////////
			// �������ָ��
			if (args[0].equalsIgnoreCase("arena")) {
				if (!p.hasPermission("ArenaCommand.Arena")) {
					p.sendMessage("��cȨ�޲���");
					return true;
				}
				//////////////////////////////////////////
				//////// ArenaCommand arena list///////////
				//////////////////////////////////////////
				if (lenth == 2 && args[1].equalsIgnoreCase("list")) {
					p.sendMessage("��b------�����б�------");
					if (plugin.getArenas().isEmpty()) {
						p.sendMessage("��c        ��");
						p.sendMessage("��c       NOPE");
					} else {
						plugin.getArenas().keySet().forEach(name -> {
							p.sendMessage(name);
						});

					}
					p.sendMessage("��b---------------------");
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
						p.sendMessage("��b��������Ѿ�������");
						return true;
					}
					p.sendMessage("��d���Ҽ��������贴����������½�");
					Listener lis = new Listener() {
						Location loc1;

						@EventHandler
						public void click(PlayerInteractEvent e) {
							if (!e.getPlayer().equals(p) || e.getClickedBlock() == null
									|| e.getAction() != Action.RIGHT_CLICK_BLOCK) {
								return;
							}
							loc1 = e.getClickedBlock().getLocation();
							p.sendMessage("��b��һ�����������:��d" + loc1.getX() + "," + loc1.getY() + "," + loc1.getZ() + "");
							e.setCancelled(true);
							HandlerList.unregisterAll(this);
							p.sendMessage("��b���Ҽ������������Ͻ�");
							Listener lis = new Listener() {
								Location loc2;

								@EventHandler
								public void click(PlayerInteractEvent e) {
									loc2 = e.getClickedBlock().getLocation();
									if (!e.getPlayer().equals(p) || e.getClickedBlock() == null
											|| e.getAction() != Action.RIGHT_CLICK_BLOCK || loc1.equals(loc2)) {
										return;
									}

									p.sendMessage("��b�ڶ������������(��d" + loc2.getBlockX() + "��b,��d" + loc2.getBlockY()
											+ "��b,��d" + loc2.getBlockZ() + "��b)");
									e.setCancelled(true);
									HandlerList.unregisterAll(this);
									Arena arena = new Arena(arenaname, loc1.getWorld(), loc1, loc2);
									arena.saveToConfig(plugin);
									plugin.getArenas().put(arenaname, arena);
									p.sendMessage("��b�����d" + arenaname + "��b�Ѵ������");
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
						p.sendMessage("��b������ɾ��");
					} else {
						p.sendMessage("��b���򲻴���");
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
						p.sendMessage("��b���򲻴���");
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
						p.sendMessage("��b���򲻴���");
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
			// �����ָ�����
			else if (args[0].equalsIgnoreCase("command")) {
				if (!p.hasPermission("ArenaCommand.Command")) {
					p.sendMessage("��cȨ�޲���");
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
							p.sendMessage("��d/ArenaCommand command <������> add <command> xxx");
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
							p.sendMessage("��b�ѳɹ����d" + arenaname + "��b��������뿪ָ���e" + addcmd);
						}else {
							String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".Commands");
							plugin.getConfig().set("Arena." + arenaname + ".Commands",
									fircmd == null ? addcmd : fircmd + ";" + addcmd);
							arena.addCommand(addcmd);
							p.sendMessage("��b�ѳɹ����d" + arenaname + "��b�������ָ���e" + addcmd);
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
						p.sendMessage("��b�������d" + arenaname + "��b���������ָ��");
						return true;
					}
					//////////////////////////////////////////
					///////// ArenaCommand command list//////
					//////////////////////////////////////////
					else if (argcmd.equalsIgnoreCase("list")) {
						p.sendMessage("��b�����d" + arenaname + "��b���󶨵�������:");
						p.sendMessage(arena.getCommandInfo());
						return true;
					}
					////////////////////////////////////////////
					//////////// ArenaCommand command round/////
					////////////////////////////////////////////
					else if (argcmd.equalsIgnoreCase("round")) {
						if (lenth == 3) {
							p.sendMessage("��d/ArenaCommand command <������> round <����ʱ��>");
							return true;
						}
						Long round = Long.valueOf(args[3]);
						arena.setRound(round);
						p.sendMessage("��b�����������d" + arenaname + "��bָ����������Ϊ" + round);
						return true;
					}
					///////////////////////////////
					///////////////////////////////
					else {
						this.sendCommandHelp(p);
						return true;
					}
				} else {
					p.sendMessage("��b�����򲻴���");
					return true;
				}
			}
			//////////////////////////////////////////////////////////
			////// �����������
			else if (args[0].equalsIgnoreCase("admin")) {
				if (!p.hasPermission("ArenaCommand.Admin")) {
					p.sendMessage("��cȨ�޲���");
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
					p.sendMessage("��b[��b��l����ָ���b]��a����������");
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
		p.sendMessage("��b---------------ArenaCommand-----------------");
		p.sendMessage("��b�������:");
		p.sendMessage("��e/ac arena   --�鿴����ָ�����");
		p.sendMessage("��e/ac command --�鿴ָ����ذ���");
		p.sendMessage("��e/ac admin   --�鿴����������");
		p.sendMessage("��b--------------------------------------");
	}

	private void sendArenaHelp(Player p) {
		p.sendMessage("��b---------------ArenaCommand-----------------");
		p.sendMessage("��b�������:");
		p.sendMessage("��e/ac arena list ��f--�鿴�����Ѵ����õ�����");
		p.sendMessage("��b/ac arena ��d<��������> ��acreate  ��f--����һ������");
		p.sendMessage("��b/ac arena ��d<��������> ��aremove  ��f--ɾ��һ������");
		p.sendMessage("��b/ac arena ��d<��������> ��ainfo  ��f--�鿴ĳ�������Ϣ");
		p.sendMessage("��b/ac arena ��d<��������> ��atp  ��f--������������");
		p.sendMessage("��b-------------------------------------");
	}

	private void sendCommandHelp(Player p) {
		p.sendMessage("��bָ�����:");
		p.sendMessage("��b/ac command ��d<��������> ��aadd xxx xxx  ��f--��ӽ�����뿪ʱ�Զ�ִ�е�ָ��");
		p.sendMessage("��b/ac command ��d<��������> ��around ��e<����ʱ��>  ��f--���ø��������ʱ����ָ�������");
		p.sendMessage("��b/ac command ��d<��������> ��aremove  ��f--�Ƴ������������ָ��");
		p.sendMessage("��b/ac command ��d<��������> ��alist  ��f--�г����������󶨵�ָ��");
	}

	private void sendAdminHelp(Player p) {
		p.sendMessage("��b�������ָ��");
		p.sendMessage("��e/ac admin reload ��f--���ز��");
	}

}
