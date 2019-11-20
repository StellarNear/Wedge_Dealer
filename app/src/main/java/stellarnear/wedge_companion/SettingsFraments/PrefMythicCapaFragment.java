package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

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

    public void addMythicCapaList(PreferenceCategory common,PreferenceCategory all,PreferenceCategory champ,PreferenceCategory hiero) {
           for (MythicCapacity capacity : pj.getAllMythicCapacities().getAllMythicCapacitiesList()) {
            SwitchPreference switch_feat = new SwitchPreference(mC);
            switch_feat.setKey("switch_"+capacity.getId());
            switch_feat.setTitle(capacity.getName());
            switch_feat.setSummary(capacity.getDescr());
            switch_feat.setDefaultValue(true);
            if (capacity.getType().contains("Commun")) {
                common.addPreference(switch_feat);
            }  else if (capacity.getType().contains("Universelle")) {
                all.addPreference(switch_feat);
            } else if (capacity.getType().equals("Champion")) {
                champ.addPreference(switch_feat);
            }else if (capacity.getType().equals("Hiérophante")) {
                hiero.addPreference(switch_feat);
            }
        }
    }
}
