package cn.BlockMC.Zao_Hon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cn.BlockMC.Zao_Hon.Arena;

public class ArenaEvent extends Event{
	private static final HandlerList HANDLERS = new HandlerList();
	private final Arena arena;
	
	public ArenaEvent(final Arena arena){
		this.arena = arena;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    public Arena getArena() {
    	return arena;
    }
}
