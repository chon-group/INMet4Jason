package group.chon.pythia.inmetGovBR;

import java.util.ArrayList;
import java.util.Objects;

/**
 * An Object that stores a List of Weather Alerts issued by INMET::AlertAS Service.
 *
 * @author Nilson Lazarin
 */
public class InmetAlertsArray {
    private ArrayList<InmetAlert> alertArray = new ArrayList<>();
    private Boolean hasNewItem = false;

    /**
     * Informs if it has a new alert issued
     *
     * @return TRUE if exists, a new alert was issued
     */
    public Boolean getHasNewItem() {
        return hasNewItem;
    }

    /**
     * Set if exists a new alert
     *
     * @param hasNewItem a boolean value
     */
    public void setHasNewItem(Boolean hasNewItem) {
        this.hasNewItem = hasNewItem;
    }

    /**
     * Register a new alert
     *
     * @param alert this parameter indicate the alert id
     * @return TRUE if this alert no already exists
     */
    public boolean addItem(InmetAlert alert) {
        if (!alertExists(alert.getId())) {
            alertArray.add(alert);
            setHasNewItem(true);
            return true;
        }else{
            System.out.print(".");
            return false;
        }
    }

    /**
     * Get a new alert
     *
     * @return an alert if exists
     */
    public InmetAlert getLastUnperceivedAlert(){
        for (int i=0; i<alertArray.size(); i++){
            if(!alertArray.get(i).getPerceived()){
                alertArray.get(i).setPerceived(true);
                if(i==alertArray.size()-1){
                    setHasNewItem(false);
                }
                return alertArray.get(i);
            }
        }
        return null;
    }

    /**
     * Checks if exists a specific alert
     *
     * @param id this parameter receives the alert id
     *
     * @return TRUE if the alert exists
     */
    public boolean alertExists(Integer id) {
        for (InmetAlert item : alertArray) {
            if (Objects.equals(item.getId(), id)) {
                return true;
            }
        }
        return false;
    }



}
