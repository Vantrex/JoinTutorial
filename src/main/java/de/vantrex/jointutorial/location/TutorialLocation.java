package de.vantrex.jointutorial.location;

import de.vantrex.jointutorial.location.message.TutorialMessage;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TutorialLocation {

    private Location location;
    private final List<TutorialMessage> messages = new ArrayList<>();

    public TutorialLocation(Location location, TutorialMessage message) {
        this.location = location;
        this.messages.add(message);
    }

    public TutorialLocation(Location location, List<TutorialMessage> messages) {
        this.location = location;
        for (TutorialMessage message : messages) {
            this.messages.add(this.messages.size(), message);
        }
    }

    public void addMessage(TutorialMessage message) {
        addMessage(messages.size(), message);
    }

    public void addMessage(int index, TutorialMessage message) {
        this.messages.add(index, message);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public List<TutorialMessage> getMessages() {
        return messages;
    }
}
