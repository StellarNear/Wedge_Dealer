package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.MythicCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;

public class PrefMythicCapaFragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;

    public PrefMythicCapaFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addMythicCapaList(PreferenceScreen screen) {
        Map<String, PreferenceCategory> map = buildCategoryList(screen);
           for (MythicCapacity capacity : pj.getAllMythicCapacities().getAllMythicCapacitiesList()) {
            addCapa(capacity,map.get(capacity.getType()));
        }
    }

    private Map<String, PreferenceCategory> buildCategoryList(PreferenceScreen screen) {
        Map<String, PreferenceCategory> mapTypeCat=new HashMap<>();
        for (MythicCapacity capacity : pj.getAllMythicCapacities().getAllMythicCapacitiesList()) {
            if(mapTypeCat.get(capacity.getType())==null){
                PreferenceCategory newType = new PreferenceCategory(mC);
                newType.setTitle(capacity.getType());
                newType.setKey(capacity.getType());
                screen.addPreference(newType);
                mapTypeCat.put(capacity.getType(),newType);
            }
        }
        return mapTypeCat;
    }

    private void addCapa(MythicCapacity capacity, PreferenceCategory category) {
        SwitchPreference switch_capa = new SwitchPreference(mC);
        switch_capa.setKey("switch_"+capacity.getId()+pj.getID());
        switch_capa.setTitle(capacity.getName());
        switch_capa.setSummary(capacity.getDescr());
        switch_capa.setDefaultValue(true);
        category.addPreference(switch_capa);
    }
}
