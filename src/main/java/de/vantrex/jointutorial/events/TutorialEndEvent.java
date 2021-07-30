package de.vantrex.jointutorial.events;

import de.vantrex.jointutorial.player.TutorialPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TutorialEndEvent extends PlayerEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final TutorialPlayer tutorialPlayer;

    public TutorialEndEvent(Player who, TutorialPlayer tutorialPlayer) {
        super(who);
        this.tutorialPlayer = tutorialPlayer;
    }

    public TutorialPlayer getTutorialPlayer() {
        return tutorialPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
