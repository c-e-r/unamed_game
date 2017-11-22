package unamedgame.time;

import java.io.Serializable;

/**
 * 
 */

/**
 * A class to observe time and allow other classes to do things when time
 * passes.
 * 
 * @author c-e-r
 * @version 0.0.1
 *
 */
public abstract class TimeListener implements Serializable {
    
    private static final long serialVersionUID = -5454029316475387373L;
    private boolean delete;

    /**
     * Does something when TimeListener is triggered.
     */
    protected abstract void action();

    /**
     * Sets that the TimeListener should be deleted.
     */
    public void setDelete() {
        delete = true;
    }

    /**
     * Gets if the TimeListener should be deleted.
     * @return if the TimeListener should be deleted
     */
    public boolean getDelete() {
        return delete;
    }

    /**
     * If the Listener is not deleted call action.
     */
    public void entityEvent() {
        if (!delete) {
            action();
        }
    }
}
