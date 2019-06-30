package cn.BlockMC.Zao_Hon;

import java.util.Iterator;

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
						Iterator<Arena> it = plugin.getArenas().iterator();
						while (it.hasNext()) {
							p.sendMessage(it.next().getName());
						}
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
				/////////////////////////////////////////
				//////// ArenaCommand arena create////////
				/////////////////////////////////////////
				if (argcmd.equalsIgnoreCase("create")) {
					for (Arena arena : plugin.getArenas()) {
						if (arena.getName().equals(arenaname)) {
							p.sendMessage("��b��������Ѿ�������");
							return true;
						}
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
									plugin.getArenas().add(arena);
									p.sendMessage("��b�����d" + arenaname + "��b�Ѵ������");
									return;
								}
							};
							plugin.getServer().getPluginManager().registerEvents(lis, plugin);
						}
					};
					plugin.getServer().getPluginManager().registerEvents(lis, plugin);

					// if (plugin.getArenas().containsKey(arenaname)) {
					// p.sendMessage("��b��������Ѿ�������");
					// return true;
					// }
					// if (main.allowfirstinteract.containsValue(arenaname)
					// || main.allowsecondinteract.containsValue(arenaname)) {
					// p.sendMessage("��b�Ѿ������ڴ�����������ˣ�");
					// return true;
					// }
					// if (main.allowfirstinteract.containsKey(p.getName())
					// || main.allowsecondinteract.containsKey(p.getName())) {
					// p.sendMessage("��b�����ڴ���һ�����أ�");
					// return true;
					// }
					// main.allowfirstinteract.put(p.getName(), arenaname);
					// p.sendMessage("��d�������贴����������½�");
					// return true;
				}
				/////////////////////////////////////////
				//////// ArenaCommand arena remove////////
				/////////////////////////////////////////
				else if (argcmd.equalsIgnoreCase("remove")) {
					for (Arena arena : plugin.getArenas()) {
						if (arena.getName().equals(arenaname)) {
							plugin.getArenas().remove(arena);
							arena.stopAllPlayerBukkit();
							plugin.getConfig().set("Arena." + arenaname, null);
							p.sendMessage("��b������ɾ��");
							return true;
						}
					}
					p.sendMessage("��b���򲻴���");
					return true;
					// if (plugin.getArenas().containsKey(arenaname)) {
					// plugin.getArenas().remove(arenaname);
					// plugin.getConfig().set("Arena." + arenaname, null);
					// p.sendMessage("��b������ɾ��");
					// return true;
					// } else {
					// p.sendMessage("��b���򲻴���");
					// return true;
					// }
				}
				//////////////////////////////////////////
				///////// ArenaCommand arena info//////////
				//////////////////////////////////////////
				else if (argcmd.equalsIgnoreCase("info")) {
					for (Arena arena : plugin.getArenas()) {
						if (arena.getName().equals(arenaname)) {
							p.sendMessage(arena.returninfo());
							return true;
						}
					}
					p.sendMessage("��b���򲻴���");
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
				for (Arena arena : plugin.getArenas()) {
					if (arena.getName().equals(arenaname)) {
						//////////////////////////////////////////
						///////// ArenaCommand command add/////////
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
							String fircmd = plugin.getConfig().getString("Arena." + arenaname + ".Commands");
							plugin.getConfig().set("Arena." + arenaname + ".Commands",
									fircmd == null ? addcmd : fircmd + ";" + addcmd);
							plugin.saveConfig();
							arena.addCommand(addcmd);
							p.sendMessage("��b�ѳɹ����d" + arenaname + "��b��������ָ���e" + addcmd);
							return true;
						}
						//////////////////////////////////////////
						///////// ArenaCommand command remove//////
						//////////////////////////////////////////
						else if (argcmd.equalsIgnoreCase("remove")) {
							plugin.getConfig().set("Arena." + arenaname + ".Commands", null);
							arena.clearCommands();
							;
							plugin.saveConfig();
							p.sendMessage("��b�������d" + arenaname + "��b���������ָ��");
							return true;
						}
						//////////////////////////////////////////
						///////// ArenaCommand command list//////
						//////////////////////////////////////////
						else if (argcmd.equalsIgnoreCase("list")) {
							if (!arena.getCommands().isEmpty()) {
								p.sendMessage("��b�����d" + arenaname + "��b���󶨵�������:");
								for (String str : arena.getCommands()) {
									p.sendMessage("��b"+ str);
								}
							} else {
								p.sendMessage("��6��������ָ���");
							}
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
					}
				}
				p.sendMessage("��b�����򲻴���");
				return true;

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
		p.sendMessage("��e/ac admin   --�鿴�����������");
		p.sendMessage("��b--------------------------------------");
	}

	private void sendArenaHelp(Player p) {
		p.sendMessage("��b---------------ArenaCommand-----------------");
		p.sendMessage("��b�������:");
		p.sendMessage("��e/ac arena list ��f--�鿴�����Ѵ����õ�����");
		p.sendMessage("��b/ac arena ��d<��������> ��acreate  ��f--����һ������");
		p.sendMessage("��b/ac arena ��d<��������> ��aremove  ��f--ɾ��һ������");
		p.sendMessage("��b/ac arena ��d<��������> ��ainfo  ��f--�鿴ĳ�������Ϣ");
		p.sendMessage("��b-------------------------------------");
	}

	private void sendCommandHelp(Player p) {
		p.sendMessage("��bָ�����:");
		p.sendMessage("��b/ac command ��d<��������> ��ajoin xxx xxx  ��f--���ӽ���ʱ�Զ�ִ�е�ָ��");
		p.sendMessage("��b/ac command ��d<��������> ��aleave xxx xxx  ��f--�����뿪ʱ��ָ��");
		p.sendMessage("��b/ac command ��d<��������> ��around ��e<����ʱ��>  ��f--���ø��������ʱ����ָ�������");
		p.sendMessage("��b/ac command ��d<��������> ��aremove  ��f--�Ƴ������������ָ��");
		p.sendMessage("��b/ac command ��d<��������> ��alist  ��f--�г����������󶨵�ָ��");
	}

	private void sendAdminHelp(Player p) {
		p.sendMessage("��b�������ָ��");
		p.sendMessage("��e/ac admin reload ��f--���ز��");
	}

}