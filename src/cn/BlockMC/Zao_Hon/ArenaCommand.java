package cn.BlockMC.Zao_Hon;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import cn.BlockMC.Zao_Hon.commands.AdminCommand;
import cn.BlockMC.Zao_Hon.commands.ArenaCmdCommand;
import cn.BlockMC.Zao_Hon.commands.CommandDispatcher;

public class ArenaCommand extends JavaPlugin {
	private Boolean debug = true;
	private HashMap<String,Arena> arenas = new HashMap<String,Arena>();
	
	private Message message;
	private CommandDispatcher commandDispatcher;

	@Override
	public void onEnable() {
		instance = this;
		
		this.saveDefaultConfig();
		this.reloadConfig();
		this.debug = getConfig().getBoolean("Debug");
		this.getServer().getPluginManager().registerEvents(new ArenaEventListener(this), this);
		
		this.message = new Message(this);
		message.setLanguage(Config.LANG);
		
		
		commandDispatcher = new CommandDispatcher(this, "ArenaCommand", Message.getString("command_description_plugin"));
		commandDispatcher.registerCommand(new cn.BlockMC.Zao_Hon.commands.ArenaCommand(this));
		commandDispatcher.registerCommand(new ArenaCmdCommand(this));
		commandDispatcher.registerCommand(new AdminCommand(this));
		this.getCommand("ArenaCommand").setExecutor(commandDispatcher);
		
		Bukkit.getScheduler().runTask(this, ()->loadArena());

//		this.saverunnable.runTaskTimer(this, 12000l, 12000l);
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
		for (Arena arena : arenas.values()) {
			arena.saveToConfig(this);
			arena.stopAllPlayerBukkit();
		}
		this.arenas.clear();
		this.getLogger().info("Disable");
	}

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
			arenas.put(str,new Arena(str, this));
		}
		this.getLogger().info("总加载" + i + "个区域");
	}
	
	public HashMap<String,Arena> getArenas(){
		return this.arenas;
	}

	public void PR(String str) {
		this.getLogger().info(str);
	}
	
	public void Debug(String str) {
		if(debug){
			this.getLogger().info(str);
		}
	}
	
	private static ArenaCommand instance;
	public static ArenaCommand getInstance() {
		return instance;
	}
}
