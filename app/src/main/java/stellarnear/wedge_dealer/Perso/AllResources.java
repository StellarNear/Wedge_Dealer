package stellarnear.wedge_dealer.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.Tools;

/**
 * Created by jchatron on 02/02/2018.
 */

public class AllResources {
    private Context mC;
    private Map<String, Resource> mapIDRes = new HashMap<>();
    private List<Resource> listResources = new ArrayList<>();
    private SharedPreferences settings;
    private Tools tools = new Tools();

    public AllResources(Context mC) {
        this.mC = mC;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildResourcesList();
        refreshMaxs();
        loadCurrent();
    }

    private void buildResourcesList() {
        Resource resMyth = new Resource("Points mythiques","mythic_points",mC);
        listResources.add(resMyth);
        mapIDRes.put(resMyth.getId(), resMyth);
        Resource resLeg = new Resource("Points légendaire (Heimdall)","legendary_points",mC);
        listResources.add(resLeg);
        mapIDRes.put(resLeg.getId(), resLeg);
    }

    public List<Resource> getResourcesList() {
        return listResources;
    }

    public Resource getResource(String resourceId) {
        Resource selectedResource;
        try {
            selectedResource = mapIDRes.get(resourceId);
        } catch (Exception e) {
            selectedResource = null;
        }
        return selectedResource;
    }
    private int readResource(String key) {
        int resId = mC.getResources().getIdentifier(key.toLowerCase() + "_def", "integer", mC.getPackageName());
        return tools.toInt(settings.getString(key.toLowerCase(), String.valueOf(mC.getResources().getInteger(resId))));
    }

    public void refreshMaxs() {
        //partie from setting
        getResource("mythic_points").setMax(3+2*readResource("mythic_tier"));
        getResource("legendary_points").setMax(readResource("legendary_points"));
    }

    private void loadCurrent() {
        for (Resource res : listResources) {
            res.loadCurrentFromSettings();
        }
    }

    public void sleepReset() {
        for (Resource res : listResources) {
            res.resetCurrent();
        }
    }
}
