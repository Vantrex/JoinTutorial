package de.vantrex.jointutorial.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class TutorialMongo {

    private final MongoClient client;
    private final MongoDatabase database;

    private final MongoCollection<Document> players;
    private final MongoCollection<Document> messages;


    public TutorialMongo(JavaPlugin plugin) {

        FileConfiguration cfg = plugin.getConfig();
        if (cfg.getConfigurationSection("mongo") == null) {
            throw new RuntimeException("Missing configuration option");
        }

        ConfigurationSection section = cfg.getConfigurationSection("mongo");
        if (section.getBoolean("authentication.enabled")) {
            final MongoCredential credential = MongoCredential.createCredential(
                    section.getString("authentication.username"),
                    section.getString("authentication.database"),
                    section.getString("authentication.password").toCharArray()
            );
            this.client = new MongoClient(new ServerAddress(section.getString("host"), section.getInt("port")), Collections.singletonList(credential));
        } else {
            this.client = new MongoClient(new ServerAddress(section.getString("host"), section.getInt("port")));
        }

        this.database = this.client.getDatabase("etyrium");
        this.players = this.database.getCollection("players");
        this.messages = this.database.getCollection("messages");

    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getPlayers() {
        return players;
    }

    public MongoCollection<Document> getMessages() {
        return messages;
    }
}
