package cn.BlockMC.Zao_Hon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import cn.BlockMC.Zao_Hon.events.PlayerEnterArenaEvent;
import cn.BlockMC.Zao_Hon.events.PlayerLeaveArenaEvent;

public class Arena {
	private HashMap<Player, BukkitRunnable> playerrunnables = new HashMap<Player, BukkitRunnable>();
	private List<String> commandList = new ArrayList<String>();
	private List<String> leaveCommandList = new ArrayList<String>();
	private Set<Player> playerSet = new HashSet<Player>();
	private String name;
	private World world;
	private int minx;
	private int maxx;
	private int miny;
	private int maxy;
	private int minz;
	private int maxz;
	private long round = 0;
	private ArenaListener listener;

	public Arena(String name, World world, Location firloc, Location secloc) {
		this.name = name;
		this.world = world;
		this.minx = Math.min(firloc.getBlockX(), secloc.getBlockX());
		this.maxx = Math.max(firloc.getBlockX(), secloc.getBlockX());
		this.miny = Math.min(firloc.getBlockY(), secloc.getBlockY());
		this.maxy = Math.max(firloc.getBlockY(), secloc.getBlockY());
		this.minz = Math.min(firloc.getBlockZ(), secloc.getBlockZ());
		this.maxz = Math.max(firloc.getBlockZ(), secloc.getBlockZ());
		this.listener = new ArenaListener();
		Bukkit.getServer().getPluginManager().registerEvents(listener, ArenaCommand.getInstance());
	}

	public Arena(String name, ArenaCommand plugin) {
		String arena = "Arena." + name + ".";
		this.name = name;

		plugin.Debug("开始加载区域" + name);
		this.world = Bukkit.getWorld(plugin.getConfig().getString(arena + "World"));

		plugin.Debug("世界名:" + plugin.getConfig().getString(arena + "World"));
		try {
			plugin.Debug("世界:" + world.getName());
		} catch (NullPointerException e) {
			plugin.Debug("加载世界时出错");
			e.printStackTrace();
		}
		this.minx = plugin.getConfig().getInt(arena + "Minx");
		this.maxx = plugin.getConfig().getInt(arena + "Maxx");
		this.miny = plugin.getConfig().getInt(arena + "Miny");
		this.maxy = plugin.getConfig().getInt(arena + "Maxy");
		this.minz = plugin.getConfig().getInt(arena + "Minz");
		this.maxz = plugin.getConfig().getInt(arena + "Maxz");
		this.round = plugin.getConfig().getLong(arena + "Round");
		plugin.Debug("指令周期:" + round);

		for (String cmd : plugin.getConfig().getString(arena + "Commands").split(";")) {
			this.addCommand(cmd);
		}
		for (String cmd : plugin.getConfig().getString(arena + "LeaveCommands").split(";")) {
			this.addLeaveCommand(cmd);
		}

		plugin.Debug("指令:" + commandList);
		plugin.Debug("坐标:(" + minx + "," + miny + "," + minz + "),(" + maxx + "," + maxy + "," + maxz + ")");

		plugin.PR("已成功加载区域[" + name + "](" + world.getName() + ")(" + minx + "," + miny + "," + minz + ")(" + maxx + ","
				+ maxy + "," + maxz + ")");
		this.listener = new ArenaListener();
		Bukkit.getServer().getPluginManager().registerEvents(listener, ArenaCommand.getInstance());
	}

	public boolean contain(Location loc) {
		return loc.getWorld().equals(world) && loc.getX() >= minx && loc.getY() >= miny && loc.getZ() >= minz
				&& loc.getX() <= maxx && loc.getY() <= maxy && loc.getZ() <= maxz;
	}

	public String getName() {
		return this.name;
	}

	public String[] returninfo() {
		String[] str = new String[3];
		str[0] = "§b区域名:§a" + this.name;
		str[1] = "§b坐标:[§a" + this.world.getName() + "§b],(§d" + this.minx + "§b,§d" + this.miny + "§b,§d" + this.minz
				+ "§b),(§d" + this.maxx + "§b,§d" + this.maxy + "§b,§d" + this.maxz + "§b)";
		if (!commandList.isEmpty()) {
			String commands = "";
			for (String command : commandList) {
				commands = commands + ";" + command;
			}
			commands = commands.substring(1);
			str[2] = "§b进入指令:§d" + commands == "" ? "无" : commands;
		}
		
		if (!leaveCommandList.isEmpty()) {
			String commands = "";
			for (String command : leaveCommandList) {
				commands = commands + ";" + command;
			}
			commands = commands.substring(1);
			str[3] = "§b离开指令:§d" + commands == "" ? "无" : commands;
		}
		
		return str;
	}
	
	public String[] getCommandInfo() {
		String[] str = new String[2];
		if (!commandList.isEmpty()) {
			String commands = "";
			for (String command : commandList) {
				commands = commands + ";" + command;
			}
			commands = commands.substring(1);
			str[0] = "§b进入指令:§d" + commands == "" ? "无" : commands;
		}
		
		if (!leaveCommandList.isEmpty()) {
			String commands = "";
			for (String command : leaveCommandList) {
				commands = commands + ";" + command;
			}
			commands = commands.substring(1);
			str[1] = "§b离开指令:§d" + commands == "" ? "无" : commands;
		}
		
		return str;
	}

	public Set<Player> getPlayerSet() {
		return this.playerSet;
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

//	public void addPlayer(Player p) {
//		this.playerSet.add(p);
//	}
//
//	public void removePlayer(Player p) {
//		this.playerSet.remove(p);
//	}

	public void setRound(Long round) {
		this.round = round;
	}

	public void addCommand(String str) {
		this.commandList.add(str);
	}

	public void addLeaveCommand(String str) {
		this.leaveCommandList.add(str);
	}

	public void clearCommands() {
		this.commandList.clear();
		this.leaveCommandList.clear();
	}

	public void runPlayerBukkit(Player p, ArenaCommand plugin) {
		playerrunnables.put(p, new BukkitRunnable() {

			@Override
			public void run() {
				runCommands(p, commandList);
			}
		});
		if (round == 0) {
			playerrunnables.get(p).run();
		} else {
			playerrunnables.get(p).runTaskTimer(plugin, 0, round * 20);
		}
	}

	public void runCommands(Player p, List<String> commands) {
		Location loc = p.getLocation();
		for (String cmd : commands) {
			cmd = cmd.replace("%player%", p.getName()).replace("%locx%", Integer.toString(loc.getBlockX()))
					.replace("%locy%", Integer.toString(loc.getBlockY()))
					.replace("%locz%", Integer.toString(loc.getBlockZ()));
			if (cmd.startsWith("~")) {
				cmd = cmd.substring(1, cmd.length());
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
			} else if (cmd.startsWith("!")) {
				cmd = cmd.substring(1, cmd.length());
				if (!p.isOp()) {
					p.setOp(true);
					p.performCommand(cmd);
					p.setOp(false);
				} else {
					p.performCommand(cmd);
				}
			} else {
				p.performCommand(cmd);
			}
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

	private class ArenaListener implements Listener {

		@EventHandler
		public void onPlayerMove(PlayerMoveEvent e) {
			Location loc = e.getTo();
			Player p = e.getPlayer();
			if (contain(loc)) {
				if (playerSet.add(p)) {
					ArenaCommand.getInstance().getServer().getPluginManager()
							.callEvent(new PlayerEnterArenaEvent(Arena.this, p));
					runPlayerBukkit(p, ArenaCommand.getInstance());
				}
			} else {
				if (playerSet.remove(p)) {
					ArenaCommand.getInstance().getServer().getPluginManager()
							.callEvent(new PlayerLeaveArenaEvent(Arena.this, p));
					stopPlayerBukkit(p);
					runCommands(p, leaveCommandList);
				}
			}

		}

		@EventHandler
		public void leave(PlayerQuitEvent e) {
			Player p = e.getPlayer();
			stopPlayerBukkit(p);
			playerSet.remove(p);
		}

	}
}
