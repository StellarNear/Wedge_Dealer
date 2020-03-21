package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class PrefCapaFragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private Tools tools=Tools.getTools();
    public PrefCapaFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addCapaList(PreferenceScreen screen) {
        screen.setOrderingAsAdded(true);
        Map<String, PreferenceCategory> map = buildCategoryList(screen);
        for (Capacity capacity : pj.getAllCapacities().getAllCapacitiesList()) {
            putCapa(capacity,map.get(capacity.getType()));
        }
    }

    private Map<String, PreferenceCategory> buildCategoryList(PreferenceScreen screen) {
        Map<String, PreferenceCategory> mapTypeCat=new HashMap<>();
        for (Capacity capacity : pj.getAllCapacities().getAllCapacitiesList()) {
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


    private void putCapa(final Capacity capacity, PreferenceCategory category) {
        SwitchPreference switch_feat = new SwitchPreference(mC);
        switch_feat.setKey("switch_" + capacity.getId()+pj.getID());
        switch_feat.setTitle(capacity.getName());
        String descr="";
        if(capacity.getDailyUse()!=0){
            descr+=capacity.getDailyUse()+"/j ";
        }
        if (capacity.getValue()!=0){
            descr+="Valeur : "+capacity.getValue();
        }
        if(!descr.equalsIgnoreCase("")){
            descr+="\n";
        }
        descr+=capacity.getDescr();
        switch_feat.setSummary(descr);
        switch_feat.setDefaultValue(true);
        category.addPreference(switch_feat);
    }
}
