package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.text.InputType;

import stellarnear.wedge_companion.EditTextPreference;
import stellarnear.wedge_companion.Perso.Feat;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;


public class PrefFeatFragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;

    public PrefFeatFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addFeatsList(PreferenceCategory magic, PreferenceCategory atk, PreferenceCategory def, PreferenceCategory other) {
        for (Feat feat : pj.getAllFeats().getFeatsList()) {
            SwitchPreference switch_feat = new SwitchPreference(mC);
            switch_feat.setKey("switch_" + feat.getId() + pj.getID());
            switch_feat.setTitle(feat.getName());
            switch_feat.setSummary(feat.getDescr());
            switch_feat.setDefaultValue(feat.isActive());
            if (feat.getType().contains("feat_magic")) {
                magic.addPreference(switch_feat);
            } else if (feat.getType().contains("feat_atk")) {
                atk.addPreference(switch_feat);
                if (feat.getId().equalsIgnoreCase("feat_aim")) {
                    EditTextPreference aimVal = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
                    aimVal.setTitle("Valeur de Viser");
                    aimVal.setKey("feat_aim_val");
                    aimVal.setDefaultValue(String.valueOf(mC.getResources().getInteger(R.integer.feat_aim_val_def)));
                    aimVal.setSummary("Valeur : %s");
                    atk.addPreference(aimVal);
                } else if (feat.getId().equalsIgnoreCase("feat_manyshot_suprem")) {
                    EditTextPreference manyval = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
                    manyval.setTitle("Valeur de Feu nourri supreme");
                    manyval.setKey("feat_manyshot_suprem_val");
                    manyval.setDefaultValue(String.valueOf(mC.getResources().getInteger(R.integer.feat_manyshot_suprem_val_def)));
                    manyval.setSummary("Valeur : %s");
                    atk.addPreference(manyval);
                } else if (feat.getId().equalsIgnoreCase("feat_power_atk")) {
                    EditTextPreference aimVal = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
                    aimVal.setTitle("Valeur d'attaque en puissance");
                    aimVal.setKey("feat_power_atk_val");
                    int defValID = mC.getResources().getIdentifier("feat_power_atk_val_def" + PersoManager.getPJSuffix(), "integer", mC.getPackageName());
                    aimVal.setDefaultValue(String.valueOf(mC.getResources().getInteger(defValID)));
                    aimVal.setSummary("Valeur : %s");
                    atk.addPreference(aimVal);
                }
            } else if (feat.getType().contains("feat_def")) {
                def.addPreference(switch_feat);
            } else if (feat.getType().contains("feat_other")) {
                other.addPreference(switch_feat);
            }
        }
    }
}
