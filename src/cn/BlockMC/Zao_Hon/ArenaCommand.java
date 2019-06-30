package cn.BlockMC.Zao_Hon;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaCommand extends JavaPlugin {
	private ArenaCommand plugin = this;
	private Boolean debug = false;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
		this.debug = getConfig().getBoolean("Debug");
		this.getServer().getPluginManager().registerEvents(new ArenaEventListener(this), this);
		this.getCommand("ArenaCommand").setExecutor(new Commands(this));
		Bukkit.getScheduler().runTask(this, loadrunnable);

		this.saverunnable.runTaskTimer(this, 12000l, 12000l);
		PR("========================");
		PR("      ArenaCommand          ");
		PR("     Version: " + this.getDescription().getVersion());
		PR("     Author:Zao_hon           ");
		PR("========================");

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("servers", () -> "Spigot"));

	}

	@Override
	public void onDisable() {
		for (Arena arena : arenas) {
			arena.saveToConfig(this);
		}
		this.arenas.clear();
		this.getLogger().info("Disable");
	}

	private Runnable loadrunnable = new Runnable() {

		@Override
		public void run() {
			loadArena();
		}

	};

	private BukkitRunnable saverunnable = new BukkitRunnable() {
		@Override
		public void run() {
			for (Arena arena : arenas) {
				arena.saveToConfig(plugin);
			}
		}
	};
	private HashSet<Arena> arenas = new HashSet<Arena>();

	public void loadArena() {
		this.getLogger().info("开始加载区域...");
		if (!this.getConfig().getKeys(false).contains("Arena")) {
			this.getLogger().info("无区域");
			return;
		}
		ConfigurationSection section = this.getConfig().getConfigurationSection("Arena");
		int i = 0;
		for (String str : section.getKeys(false)) {
			i++;
			arenas.add(new Arena(str, this));
		}
		this.getLogger().info("总加载" + i + "个区域");
	}

	public HashSet<Arena> getArenas() {
		return this.arenas;
	}

	public void PR(String str) {
		this.getLogger().info(str);
	}

	public Boolean isDebug() {
		return debug;
	}
}
