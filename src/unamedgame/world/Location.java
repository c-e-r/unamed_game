package unamedgame.world;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private String name;
    private String description;
    private String eventFile;

    public Location(String name, String description, String eventFile) {
        super();
        this.name = name;
        this.description = description;
        this.eventFile = eventFile;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the events
     */
    public String getEventFile() {
        return eventFile;
    }

    /**
     * @param events
     *            the events to set
     */
    public void setEventFile(String eventFile) {
        this.eventFile = eventFile;
    }
}
