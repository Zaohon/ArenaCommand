package cn.BlockMC.Zao_Hon.events;

import org.bukkit.entity.Player;

import cn.BlockMC.Zao_Hon.Arena;

public class PlayerEnterArenaEvent extends ArenaEvent{
	private final Player player;

	public PlayerEnterArenaEvent(final Arena arena,final Player player) {
		super(arena);
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
