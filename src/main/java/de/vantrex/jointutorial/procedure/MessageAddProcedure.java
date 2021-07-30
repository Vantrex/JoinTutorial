package de.vantrex.jointutorial.procedure;

import de.vantrex.jointutorial.location.TutorialLocation;

import java.util.ArrayList;
import java.util.List;

public class MessageAddProcedure {

    private final TutorialLocation tutorialLocation;

    private final List<String> messages = new ArrayList<>();

    public MessageAddProcedure(TutorialLocation tutorialLocation) {
        this.tutorialLocation = tutorialLocation;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public TutorialLocation getTutorialLocation() {
        return tutorialLocation;
    }

    public List<String> getMessages() {
        return messages;
    }
}
