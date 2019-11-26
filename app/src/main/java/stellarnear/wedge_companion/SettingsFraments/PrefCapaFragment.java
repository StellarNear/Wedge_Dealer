package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;

public class PrefCapaFragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;

    public PrefCapaFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addCapaList(PreferenceCategory druid,PreferenceCategory archer,PreferenceCategory oracle,PreferenceCategory other) {
        for (Capacity capacity : pj.getAllCapacities().getAllCapacitiesList()) {
            switch (capacity.getType()){
                case "druid":
                    putCapa(capacity,druid);
                    break;
                case "archer":
                    putCapa(capacity,archer);
                    break;
                case "oracle":
                    putCapa(capacity,oracle);
                    break;
                case "other":
                default:
                    putCapa(capacity,other);
                    break;
            }
        }
    }

    private void putCapa(Capacity capacity, PreferenceCategory category) {
        SwitchPreference switch_feat = new SwitchPreference(mC);
        switch_feat.setKey("switch_" + capacity.getId()+pj.getID());
        switch_feat.setTitle(capacity.getName());
        switch_feat.setSummary(capacity.getDescr());
        switch_feat.setDefaultValue(true);
        category.addPreference(switch_feat);
    }
}
