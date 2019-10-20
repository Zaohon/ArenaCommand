package cn.BlockMC.Zao_Hon.events;

import org.bukkit.entity.Player;

import cn.BlockMC.Zao_Hon.Arena;

public class PlayerLeaveArenaEvent extends ArenaEvent {
	private final Player player;

	public PlayerLeaveArenaEvent(final Arena arena, final Player player) {
		super(arena);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
