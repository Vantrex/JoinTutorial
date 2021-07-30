package de.vantrex.jointutorial.listeners;

import de.vantrex.jointutorial.location.TutorialLocation;
import de.vantrex.jointutorial.location.message.TutorialMessage;
import de.vantrex.jointutorial.player.PlayerUtil;
import de.vantrex.jointutorial.player.TutorialPlayer;
import de.vantrex.jointutorial.procedure.LocationCreateProcedure;
import de.vantrex.jointutorial.procedure.MessageAddProcedure;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.spigotmc.event.entity.EntityDismountEvent;

import static de.vantrex.jointutorial.handler.TutorialHandler.*;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        INSTANCE.addPlayer(player);
        INSTANCE.getPlayers()
                .stream().filter(TutorialPlayer::isInTutorial)
                .forEach(player1 -> {
                    final Player bukkitPlayer = Bukkit.getPlayer(player1.getUUID());
                    if (bukkitPlayer == player)
                        return;
                    PlayerUtil.hidePlayer(bukkitPlayer, player);
                    if (false) { // VANISH CHECK
                        return;
                    }
                    PlayerUtil.hidePlayer(player, bukkitPlayer);
                });
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        TutorialPlayer tutorialPlayer = INSTANCE.removePlayer(player);
        if (tutorialPlayer.isInTutorial()) {
            tutorialPlayer.getTask().cancel();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (INSTANCE.isCreateProcedure(player)) {
            event.setCancelled(true);
            LocationCreateProcedure procedure = INSTANCE.getCreateProcedure(player);
            if (event.getMessage().equalsIgnoreCase("cancel")) {
                INSTANCE.addLocation(procedure.getIndex(), new TutorialLocation(procedure.getLocation(), new TutorialMessage(procedure.getMessages())));
                INSTANCE.removeCreateProcedure(player);
            } else {
                procedure.addMessage(event.getMessage());
            }
            return;
        }
        if (INSTANCE.isAddMessageProcedure(player)) {
            event.setCancelled(true);
            MessageAddProcedure procedure = INSTANCE.getAddMessageProcedure(player);
            if (event.getMessage().equals("cancel")) {
                INSTANCE.removeAddMessageProcedure(player);
                return;
            }
            procedure.addMessage(event.getMessage());
        }
        event.getRecipients().removeIf(recipient -> INSTANCE.getPlayer(recipient).isInTutorial());
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        if (INSTANCE.getPlayer(event.getPlayer()).isInTutorial())
            event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (INSTANCE.getPlayer(player).isInTutorial()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        if (INSTANCE.getPlayer(event.getPlayer()).isInTutorial())
            event.setCancelled(true);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        final Player player = (Player) event.getEntity();
        if (INSTANCE.getPlayer(player).isInTutorial()) {
            if (INSTANCE.getPlayer(player).getTask().isEjecting())
                return;
            event.setCancelled(true); // only working on paper
        }
    }
}