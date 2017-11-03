/**
 * 
 */
package unamedgame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author c-e-r
 *
 */
public class Quest {
    private String questName;
    private List<String> questText;
    private boolean completed;

    public Quest(String questName) {
        this.questName = questName;
        questText = new ArrayList<String>();
    }

    public void appendText(String text) {
        questText.add(text);
    }

    /**
     * @return the questName
     */
    public String getQuestName() {
        return questName;
    }

    public String getLogInfo() {
        String info = "";
        for (String string : questText) {
            info = info + string + "\n\n";
        }
        return info;
    }
    
    public void setCompleted() {
        completed = true;
    }
    
    public boolean isCompleted() {
        return completed;
    }

}
