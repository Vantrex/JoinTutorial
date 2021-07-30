package de.vantrex.jointutorial.handler;

import com.google.common.collect.ImmutableSet;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import de.vantrex.jointutorial.TutorialPlugin;
import de.vantrex.jointutorial.location.TutorialLocation;
import de.vantrex.jointutorial.location.message.TutorialMessage;
import de.vantrex.jointutorial.player.TutorialPlayer;
import de.vantrex.jointutorial.procedure.LocationCreateProcedure;
import de.vantrex.jointutorial.procedure.MessageAddProcedure;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TutorialHandler {


    public static TutorialHandler INSTANCE;

    private final Map<UUID, TutorialPlayer> players = new ConcurrentHashMap<>();
    private final List<TutorialLocation> locations = new ArrayList<>();
    private final Map<Player, LocationCreateProcedure> createProcedures = new HashMap<>();
    private final Map<Player, MessageAddProcedure> messageAddProcedures = new HashMap<>();

    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public void init() {
        INSTANCE = this;
        EXECUTOR_SERVICE.scheduleWithFixedDelay(this::saveTask, 20, 20, TimeUnit.SECONDS);

        new BukkitRunnable() {
            @Override
            public void run() {
                players.values().stream()
                        .filter(TutorialPlayer::isInTutorial)
                        .forEach(player -> player.getTask().run());
            }
        }.runTaskTimer(TutorialPlugin.getInstance(), 20 * 2, 20 * 3);

        try (MongoCursor<Document> cursor = TutorialPlugin.getInstance().getMongo().getMessages().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                List<TutorialMessage> messages = new ArrayList<>();
                Document messagesDocument = (Document) document.get("messages");
                int size = messagesDocument.getInteger("size");
                for (int i = 0; i < size; i++) {
                    List<String> list = messagesDocument.getList(String.valueOf(i), String.class);
                    messages.add(new TutorialMessage(list));
                }
                TutorialLocation location = new TutorialLocation(TutorialPlugin.stringToLocation(document.getString("location")), messages);
                locations.add(location);
            }
        }
    }

    public void save() {
        TutorialPlugin.getInstance().getMongo().getMessages().deleteMany(new BasicDBObject());
        for (TutorialLocation location : this.locations) {
            Document document = new Document();
            Document messageDocument = new Document();
            messageDocument.put("size", location.getMessages().size());
            for (int i = 0; i < location.getMessages().size(); i++) {
                messageDocument.put(String.valueOf(i), location.getMessages().get(i).getMessage());
            }
            document.put("messages", messageDocument);
            document.put("location", TutorialPlugin.locationToString(location.getLocation()));
            TutorialPlugin.getInstance().getMongo().getMessages().insertOne(document);
        }
    }

    public TutorialLocation getLocation(int index) {
        if (this.locations.size() <= index)
            return null;
        return this.locations.get(index);
    }

    public void addCreateProcedure(final Player player, int index) {
        this.createProcedures.put(player, new LocationCreateProcedure(index, player.getLocation().clone()));
    }

    public void addAddMessageProcedure(final Player player, MessageAddProcedure procedure) {
        this.messageAddProcedures.put(player, procedure);
    }

    public void removeAddMessageProcedure(final Player player) {
        MessageAddProcedure procedure = this.messageAddProcedures.remove(player);
        procedure.getTutorialLocation().addMessage(new TutorialMessage(procedure.getMessages()));
    }

    public MessageAddProcedure getAddMessageProcedure(final Player player) {
        return this.messageAddProcedures.get(player);
    }

    public boolean isAddMessageProcedure(final Player player) {
        return this.messageAddProcedures.containsKey(player);
    }

    public LocationCreateProcedure getCreateProcedure(final Player player) {
        return this.createProcedures.get(player);
    }

    public void removeCreateProcedure(final Player player) {
        this.createProcedures.remove(player);
    }

    public boolean isCreateProcedure(final Player player) {
        return this.createProcedures.containsKey(player);
    }

    public void addLocation(TutorialLocation location) {
        this.locations.add(location);
    }

    public void addLocation(int index, TutorialLocation location) {
        this.locations.add(index > locations.size() ? locations.size() : Math.max(index, 0), location);
    }

    public List<TutorialLocation> getLocations() {
        return locations;
    }

    public void addPlayer(final Player player) {
        this.players.put(player.getUniqueId(), new TutorialPlayer(player.getUniqueId()));
    }

    public TutorialPlayer getPlayer(final Player player) {
        return this.players.get(player.getUniqueId());
    }

    public Collection<TutorialPlayer> getPlayers() {
        return this.players.values();
    }

    public TutorialPlayer removePlayer(final Player player) {
        TutorialPlayer tutorialPlayer = this.players.remove(player.getUniqueId());
        tutorialPlayer.save();
        return tutorialPlayer;
    }

    private void saveTask() {
        ImmutableSet.copyOf(players.values())
                .stream()
                .filter(TutorialPlayer::isUpdated)
                .forEach(TutorialPlayer::save);
    }
}