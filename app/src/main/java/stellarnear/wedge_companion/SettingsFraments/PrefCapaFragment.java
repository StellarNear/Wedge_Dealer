package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.InputType;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.EditTextPreference;
import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class PrefCapaFragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private Tools tools=new Tools();
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
        switch_feat.setSummary(capacity.getDescr());
        switch_feat.setDefaultValue(true);
        category.addPreference(switch_feat);

        if(capacity.getId().equalsIgnoreCase("capacity_unraveled_mystery")) {
            EditTextPreference reducMeta = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
            reducMeta.setTitle("Valeur de reduction métamagie");
            reducMeta.setKey("capacity_unraveled_mystery_metamagic_reduc");
            reducMeta.setDefaultValue(String.valueOf(mC.getResources().getInteger(R.integer.capacity_unraveled_mystery_metamagic_reduc_def)));
            reducMeta.setSummary("Valeur : %s");
            reducMeta.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    capacity.setValue(tools.toInt(o.toString()));
                    return false;
                }
            });
            category.addPreference(reducMeta);
        }
    }
}
