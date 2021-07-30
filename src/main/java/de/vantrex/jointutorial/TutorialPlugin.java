package de.vantrex.jointutorial;

import de.vantrex.jointutorial.commands.AddMessageCommand;
import de.vantrex.jointutorial.commands.DeleteLocationCommand;
import de.vantrex.jointutorial.commands.LocationCreateCommand;
import de.vantrex.jointutorial.handler.TutorialHandler;
import de.vantrex.jointutorial.listeners.PlayerListener;
import de.vantrex.jointutorial.mongo.TutorialMongo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class TutorialPlugin extends JavaPlugin {

    private TutorialMongo mongo;

    private static TutorialPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.mongo = new TutorialMongo(this);
        new TutorialHandler().init();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("tutorialset").setExecutor(new LocationCreateCommand());
        getCommand("tutorialaddmsg").setExecutor(new AddMessageCommand());
        getCommand("tutorialdelete").setExecutor(new DeleteLocationCommand());
        //
    }

    @Override
    public void onDisable() {
        TutorialHandler.INSTANCE.save();
        this.mongo.getClient().close();
    }

    public TutorialMongo getMongo() {
        return mongo;
    }

    public static TutorialPlugin getInstance() {
        return instance;
    }

    public static String locationToString(Location l) {
        if (l == null)
            return "";
        return l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch();
    }

    public static Location stringToLocation(String s) {
        if (s == null || s.trim().equals(""))
            return null;

        final String[] parts = s.split(":");

        if (parts.length == 6)
            return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        return null;
    }

}
