package de.vantrex.jointutorial.procedure;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationCreateProcedure {

    private final int index;
    private final Location location;
    private final List<String> messages = new ArrayList<>();

    public LocationCreateProcedure(int index, Location location) {
        this.index = index;
        this.location = location;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getMessages() {
        return messages;
    }

    public int getIndex() {
        return index;
    }
}
