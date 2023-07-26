package org.example;

import java.util.ArrayList;
import java.util.Objects;

public class InmetArrayAlerts {
    ArrayList<InmetAlert> alertArray = new ArrayList<>();
    Boolean hasNewItem = false;

    public Boolean getHasNewItem() {
        return hasNewItem;
    }

    public void setHasNewItem(Boolean hasNewItem) {
        this.hasNewItem = hasNewItem;
    }

    public boolean addItem(InmetAlert alert) {
        if (!alertExists(alert.id)) {
            alertArray.add(alert);
            setHasNewItem(true);
            System.out.println("["+alert.id+"] New Inmet Alert!");
            return true;
        }else{
            System.out.print(".");
            return false;
        }
    }

    public boolean alertExists(Integer id) {
        for (InmetAlert item : alertArray) {
            if (Objects.equals(item.id, id)) {
                return true;
            }
        }
        return false;
    }

    public InmetAlert getLastAlert(){
        return alertArray.get(alertArray.size()-1);
    }
}
