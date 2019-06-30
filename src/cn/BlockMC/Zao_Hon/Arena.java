package cn.BlockMC.Zao_Hon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

public class Arena {
	private HashMap<Player, BukkitRunnable> playerrunnables = new HashMap<Player, BukkitRunnable>();
	private List<String> commandlist = new ArrayList<String>();
	private List<Player> playerlist = new ArrayList<Player>();
	private String name;
	private World world;
	private int minx;
	private int maxx;
	private int miny;
	private int maxy;
	private int minz;
	private int maxz;
	private long round = 0;

	// public Arena(String name, World world, int minx, int maxx, int miny, int
	// maxy, int minz, int maxz) {
	// this.name = name;
	// this.world = world;
	// this.minx = minx;
	// this.maxx = maxx;
	// this.miny = miny;
	// this.maxy = maxy;
	// this.minz = minz;
	// this.maxz = maxz;
	// }

	public Arena(String name, World world, Location firloc, Location secloc) {
		this.name = name;
		this.world = world;
		this.minx = Math.min(firloc.getBlockX(), secloc.getBlockX());
		this.maxx = Math.max(firloc.getBlockX(), secloc.getBlockX());
		this.miny = Math.min(firloc.getBlockY(), secloc.getBlockY());
		this.maxy = Math.max(firloc.getBlockY(), secloc.getBlockY());
		this.minz = Math.min(firloc.getBlockZ(), secloc.getBlockZ());
		this.maxz = Math.max(firloc.getBlockZ(), secloc.getBlockZ());
	}

	public Arena(String name, ArenaCommand plugin) {
		String arena = "Arena." + name + ".";
		this.name = name;
		if (plugin.isDebug())
			plugin.getLogger().info("开始加载区域" + name);
		this.world = Bukkit.getWorld(plugin.getConfig().getString(arena + "World"));
		if (plugin.isDebug()) {
			plugin.getLogger().info("世界名:" + plugin.getConfig().getString(arena + "World"));
			try {
				plugin.getLogger().info("世界:" + world.getName());
			} catch (NullPointerException e) {
				plugin.getLogger().info("加载世界时出错");
				e.printStackTrace();
			}
		}
		this.minx = plugin.getConfig().getInt(arena + "Minx");
		this.maxx = plugin.getConfig().getInt(arena + "Maxx");
		this.miny = plugin.getConfig().getInt(arena + "Miny");
		this.maxy = plugin.getConfig().getInt(arena + "Maxy");
		this.minz = plugin.getConfig().getInt(arena + "Minz");
		this.maxz = plugin.getConfig().getInt(arena + "Maxz");
		this.round = plugin.getConfig().getLong(arena + "Round");
		if (plugin.isDebug())
			plugin.getLogger().info("指令周期:" + round);
		if (plugin.getConfig().getString(arena + "Commands") != null) {
			for (String cmd : plugin.getConfig().getString(arena + "Commands").split(";")) {
				this.addCommand(cmd);
			}
		}
		if (plugin.isDebug()) {
			plugin.PR("指令:" + commandlist);
			plugin.PR("坐标:(" + minx + "," + miny + "," + minz + "),(" + maxx + "," + maxy + "," + maxz + ")");
		}
		plugin.PR("已成功加载区域[" + name + "](" + world.getName() + ")(" + minx + "," + miny + "," + minz + ")(" + maxx + ","
				+ maxy + "," + maxz + ")");
	}

	public boolean contain(Location loc) {

		if (loc.getWorld().equals(world) && loc.getX() >= minx && loc.getY() >= miny && loc.getZ() >= minz
				&& loc.getX() <= maxx && loc.getY() <= maxy && loc.getZ() <= maxz) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hadEntered(Player p) {
		if (this.playerlist.contains(p)) {
			return true;
		} else {
			return false;
		}
	}

	// public boolean hasCommand(String str) {
	// if (this.commandlist.contains(str)) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	public String getName() {
		return this.name;
	}

	public String[] returninfo() {
		String[] str = new String[3];
		str[0] = "§b区域名:§a" + this.name;
		str[1] = "§b坐标:[§a" + this.world.getName() + "§b],(§d" + this.minx + "§b,§d" + this.miny + "§b,§d" + this.minz
				+ "§b),(§d" + this.maxx + "§b,§d" + this.maxy + "§b,§d" + this.maxz + "§b)";
		if (!this.getCommands().isEmpty()) {
			String commands = "";
			for (String command : this.getCommands()) {
				commands = commands + ";" + command;
			}
			commands = commands.substring(1);
			str[2] = "§b指令:§d" + commands == "" ? "无" : commands;
		} else {
			str[2] = "§b指令:§d无";
		}
		return str;
	}

	public List<String> getCommands() {
		return this.commandlist;
	}

	public List<Player> getEnteredPlayers() {
		return this.playerlist;
	}

	public void saveToConfig(ArenaCommand plugin) {
		if (world == null) {
			plugin.getLogger().info("找不到world");
			return;
		}
		plugin.getConfig().set("Arena." + name + ".World", world.getName());
		plugin.getConfig().set("Arena." + name + ".Minx", minx);
		plugin.getConfig().set("Arena." + name + ".Maxx", maxx);
		plugin.getConfig().set("Arena." + name + ".Miny", miny);
		plugin.getConfig().set("Arena." + name + ".Maxy", maxy);
		plugin.getConfig().set("Arena." + name + ".Minz", minz);
		plugin.getConfig().set("Arena." + name + ".Maxz", maxz);
		plugin.getConfig().set("Arena." + name + ".Round", round);
		plugin.saveConfig();
	}

	public void addPlayerEnter(Player p) {
		if (this.hadEntered(p)) {
			return;
		}
		this.playerlist.add(p);
	}

	public void removePlayerEnter(Player p) {
		if (!this.hadEntered(p)) {
			return;
		}
		this.playerlist.remove(p);
	}

	public void setRound(Long round) {
		this.round = round;
	}

	public void addCommand(String str) {
		this.commandlist.add(str);
	}

	public void removeCommand(String str) {
		this.commandlist.remove(str);
	}

	public void clearCommands() {
		this.commandlist.clear();
	}

	// public void performCommands(Player p, ArenaCommand plugin) {
	// if (!this.commandlist.isEmpty()) {
	// Location loc = p.getLocation();
	// commandlist.forEach(cmd -> {
	// cmd = cmd.replace("%player%", p.getName()).replace("%locx%",
	// Integer.toString(loc.getBlockX()))
	// .replace("%locy%", Integer.toString(loc.getBlockY()))
	// .replace("%locz%", Integer.toString(loc.getBlockZ()));
	// //
	// Bukkit.broadcastMessage(cmd);
	// //
	// if (cmd.startsWith("~")) {
	// cmd = cmd.substring(1, cmd.length());
	// Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
	// } else if (cmd.startsWith("!")) {
	// cmd = cmd.substring(1, cmd.length());
	// //
	// Bukkit.broadcastMessage(cmd);
	// //
	// PermissionAttachment per = p.addAttachment(plugin, 1000);
	// per.setPermission("*", true);
	// p.performCommand(cmd);
	// per.unsetPermission("*");
	// } else {
	// p.performCommand(cmd);
	// }
	// });
	//
	// }
	//
	// }

	public void runPlayerBukkit(Player p, ArenaCommand plugin) {
		playerrunnables.put(p, new BukkitRunnable() {

			@Override
			public void run() {

				Location loc = p.getLocation();
				for (String cmd : commandlist) {
					cmd = cmd.replace("%player%", p.getName()).replace("%locx%", Integer.toString(loc.getBlockX()))
							.replace("%locy%", Integer.toString(loc.getBlockY()))
							.replace("%locz%", Integer.toString(loc.getBlockZ()));
					if (cmd.startsWith("~")) {
						cmd = cmd.substring(1, cmd.length());
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
					} else if (cmd.startsWith("!")) {
						cmd = cmd.substring(1, cmd.length());
						if(!p.isOp()){
							p.setOp(true);
							p.performCommand(cmd);
							p.setOp(false);
						}else{
							p.performCommand(cmd);
						}
					} else {
						p.performCommand(cmd);
					}
				}
			}
		});
		if (round == 0) {
			playerrunnables.get(p).run();
		} else {
			playerrunnables.get(p).runTaskTimer(plugin, 0, round * 20);
		}
	}

	public void stopAllPlayerBukkit() {
		for (Player p : playerrunnables.keySet()) {
			stopPlayerBukkit(p);
		}
	}

	public void stopPlayerBukkit(Player p) {
		if (playerrunnables.containsKey(p)) {
			try {
				playerrunnables.get(p).cancel();
			} catch (IllegalStateException e) {

			}
			playerrunnables.remove(p);
		}

	}

	public Location getLocationLow() {
		return new Location(world, minx, miny, minz);
	}

	public Location getLocationHigh() {
		return new Location(world, maxx, maxy, maxz);
	}
}
