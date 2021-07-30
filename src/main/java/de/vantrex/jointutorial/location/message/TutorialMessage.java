package de.vantrex.jointutorial.location.message;

import java.util.List;

public class TutorialMessage {

    private final List<String> message;

    public TutorialMessage(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return message;
    }
}
