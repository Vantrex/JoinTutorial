package de.vantrex.jointutorial.player;

import com.mongodb.client.model.ReplaceOptions;
import de.vantrex.jointutorial.TutorialPlugin;
import de.vantrex.jointutorial.events.TutorialStartEvent;
import de.vantrex.jointutorial.handler.TutorialHandler;
import de.vantrex.jointutorial.mongo.MongoUtil;
import de.vantrex.jointutorial.task.TutorialTask;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TutorialPlayer {

    private final UUID uuid;
    private boolean usedTutorial = false;
    private boolean updated = false;
    private final boolean async;
    private boolean ready = false;
    private boolean inTutorial;
    private TutorialTask task;

    private Location location;
    private ItemStack[] inventory;
    private ItemStack[] armor;

    public TutorialPlayer(UUID uuid) {
        this(uuid, true);
    }

    public TutorialPlayer(UUID uuid, boolean async) {
        this.uuid = uuid;
        this.async = async;
        load();
    }


    public boolean isUpdated() {
        return updated;
    }


    public TutorialTask getTask() {
        return task;
    }

    public void setUsedTutorial(boolean usedTutorial) {
        this.usedTutorial = usedTutorial;
        updated = true;
    }

    public boolean hasUsedTutorial() {
        return usedTutorial;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setInTutorial(boolean inTutorial) {
        this.inTutorial = inTutorial;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isInTutorial() {
        return inTutorial;
    }

    private void onReady() {
        if (!usedTutorial) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        return;
                    PlayerUtil.sendTitle(player, "§2§lWillkommen!", 10, 40, 10);
                    PlayerUtil.sendSubTitle(player, "§eLass uns mit dem Tutorial beginnen...", 10, 40, 10);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline())
                                return;
                            TutorialStartEvent startEvent = new TutorialStartEvent(player, TutorialPlayer.this);
                            Bukkit.getPluginManager().callEvent(startEvent);
                            if (startEvent.isCancelled())
                                return;
                            location = player.getLocation().clone();
                            inventory = player.getInventory().getContents().clone();
                            armor = player.getInventory().getArmorContents().clone();
                            player.getInventory().clear();
                            player.getInventory().setArmorContents(null);
                            player.setHealth(20);
                            player.setFoodLevel(20);
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, false, false));
                            setInTutorial(true);
                            Bukkit.getOnlinePlayers().forEach(online -> {
                                if (online == player)
                                    return;
                                PlayerUtil.hidePlayer(player, online);
                                if (false) { // VANISH CHECK
                                    return;
                                }
                                PlayerUtil.hidePlayer(online, player);
                            });
                            task = new TutorialTask(player, TutorialPlayer.this);
                            task.run();
                        }
                    }.runTaskLater(TutorialPlugin.getInstance(), 60);
                }
            }.runTaskLaterAsynchronously(TutorialPlugin.getInstance(), 35);
        }
    }

    public void setTask(TutorialTask task) {
        this.task = task;
    }

    private void load() {
        Runnable runnable = () -> {
            Document document = TutorialPlugin.getInstance().getMongo().getPlayers().find(MongoUtil.find("uuid", this.uuid.toString())).first();
            if (document == null) {
                updated = true;
                onReady();
                return;
            }
            this.usedTutorial = document.getBoolean("usedTutorial");
            if (!ready)
                ready = true;
            onReady();
        };

        if (this.async)
            TutorialHandler.EXECUTOR_SERVICE.execute(runnable);
        else
            runnable.run();
    }

    public void save() {
        Runnable runnable = () -> {
            Document document = TutorialPlugin.getInstance().getMongo().getPlayers().find(MongoUtil.find("uuid", this.uuid.toString())).first();
            if (document == null)
                document = new Document();
            document.put("usedTutorial", this.usedTutorial);
            document.put("uuid", this.uuid.toString());
            TutorialPlugin.getInstance().getMongo().getPlayers().replaceOne(MongoUtil.find("uuid", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
            this.updated = false;
        };
        if (this.async)
            TutorialHandler.EXECUTOR_SERVICE.execute(runnable);
        else
            runnable.run();
    }
}
