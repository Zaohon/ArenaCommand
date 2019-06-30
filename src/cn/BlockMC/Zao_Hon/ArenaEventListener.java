package cn.BlockMC.Zao_Hon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaEventListener implements Listener {
	private ArenaCommand plugin;

	public ArenaEventListener(ArenaCommand plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void PlayerEnterToArenaRunCommand(PlayerMoveEvent e) {
		Location loc = e.getTo();
		Player p = e.getPlayer();
		for (Arena arena : plugin.getArenas().values()) {
			if (arena.contain(loc)) {
				if (!arena.hadEntered(p)) {
					arena.runPlayerBukkit(p, plugin);
					arena.addPlayerEnter(p);
				}
			} else {
				arena.stopPlayerBukkit(p);
				arena.removePlayerEnter(p);
			}
			//////////////////////////////////////////////////
		}
		return;
	}
	@EventHandler
	public void leave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		for(Arena arena:plugin.getArenas().values()){
			arena.stopPlayerBukkit(p);
			arena.removePlayerEnter(p);
		}
	}

}
