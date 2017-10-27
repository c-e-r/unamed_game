package unamedgame.entities;

import java.io.Serializable;

/**
 * 
 */

/**
 * A class to observe entities and allow other classes to do things when stuff.
 * happens
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public abstract class EntityListener implements Serializable {

    private static final long serialVersionUID = 7751064600840910271L;
    private boolean delete;

    /**
     * A method that does something when something happens to an entity.
     * @param event
     */
    protected abstract void action(String event);

    /**
     * Sets the listener should be deleted.
     */
    public void setDelete() {
        delete = true;
    }

    /**
     * Returns if the listener should be deleted.
     * @return if the listener should be deleted
     */
    public boolean getDelete() {
        return delete;
    }

    /**
     * Activates the action if the listener is not deleted.
     * @param event the reason for calling the event
     */
    public void entityEvent(String event) {
        if (!delete) {
            action(event);
        }
    }
}
