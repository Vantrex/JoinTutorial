package de.vantrex.jointutorial.events;

import de.vantrex.jointutorial.player.TutorialPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TutorialStartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final TutorialPlayer tutorialPlayer;
    private boolean cancelled = false;

    public TutorialStartEvent(Player who, TutorialPlayer tutorialPlayer) {
        super(who);
        this.tutorialPlayer = tutorialPlayer;
    }


    public TutorialPlayer getTutorialPlayer() {
        return tutorialPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
