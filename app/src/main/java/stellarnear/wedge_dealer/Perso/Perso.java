package stellarnear.wedge_dealer.Perso;

import android.content.Context;
/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;

    public Perso(Context mC) {
        inventory = new Inventory(mC);
        allResources = new AllResources(mC);
    }

    public void refresh() {
        allResources.refreshMaxs();
    }

    public AllResources getAllResources() {
        return this.allResources;
    }

    public Integer getResourceValue(String resId){
        Resource res = allResources.getResource(resId);
        Integer value=0;
        if(res!=null){
            value=res.getCurrent();
        }
        return value;
    }

    public Inventory getInventory() {
        return inventory;
    }

}
