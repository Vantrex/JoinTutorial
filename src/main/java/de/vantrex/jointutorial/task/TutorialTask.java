package de.vantrex.jointutorial.task;

import de.vantrex.jointutorial.TutorialPlugin;
import de.vantrex.jointutorial.events.TutorialEndEvent;
import de.vantrex.jointutorial.handler.TutorialHandler;
import de.vantrex.jointutorial.location.TutorialLocation;
import de.vantrex.jointutorial.location.message.TutorialMessage;
import de.vantrex.jointutorial.player.PlayerUtil;
import de.vantrex.jointutorial.player.TutorialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TutorialTask {

    private final Queue<TutorialLocation> queue = new LinkedBlockingQueue<>();
    private final Queue<TutorialMessage> messageQueue = new LinkedBlockingQueue<>();
    private TutorialLocation current;
    private final Player player;
    private final ArmorStand armorStand;
    private final TutorialPlayer tutorialPlayer;
    private boolean ejecting = false;
    long lastTickTime = -1;
    public TutorialTask(Player player, TutorialPlayer tutorialPlayer) {
        this.player = player;
        this.tutorialPlayer = tutorialPlayer;
        this.armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        this.armorStand.setVisible(false);
        this.armorStand.setPassenger(player);
        this.armorStand.setGravity(false);

        armorStand.setMetadata("joinentity", new FixedMetadataValue(TutorialPlugin.getInstance(), 0));
        queue.addAll(TutorialHandler.INSTANCE.getLocations());
    }

    public void run() {
        if (lastTickTime > System.currentTimeMillis() - 2000) return; // replace 2000 with tick time in millis (rn 3 seconds afaik)
        if (current == null) {
            TutorialLocation next = queue.poll();
            if (next == null) {
                cancel();
                tutorialPlayer.setUsedTutorial(true);
                return;
            }
            current = next;
            messageQueue.addAll(current.getMessages());
            ejecting = true;
            armorStand.eject();
            armorStand.teleport(current.getLocation());
            player.teleport(armorStand.getLocation());
            armorStand.setPassenger(player);
            ejecting = false;
        }

        TutorialMessage message = messageQueue.poll();
        if (message == null) {
            current = null;
            return;
        }
        for (String str : message.getMessage()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
        }
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5L, 3.6F);
        lastTickTime = System.currentTimeMillis();
    }

    public boolean isEjecting() {
        return ejecting;
    }

    public void cancel() {
        armorStand.remove();
        tutorialPlayer.setInTutorial(false);
        PlayerUtil.clear(player);
        player.getInventory().setContents(tutorialPlayer.getInventory());
        player.getInventory().setArmorContents(tutorialPlayer.getArmor());
        player.teleport(tutorialPlayer.getLocation().add(0, 0.2, 0));
        player.sendMessage("Tutorial end");
        TutorialEndEvent event = new TutorialEndEvent(player, tutorialPlayer);
        Bukkit.getPluginManager().callEvent(event);
        tutorialPlayer.setTask(null);
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online == player)
                return;
            online.showPlayer(player);
            if (false) { // VANISH CHECK
                return;
            }
            player.showPlayer(online);
        });
    }
}