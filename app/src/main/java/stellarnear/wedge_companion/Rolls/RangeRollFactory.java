package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class RangeRollFactory {

    private Activity mA;
    private Context mC;
    private String mode;
    private RollList rollList;
    private SharedPreferences settings;
    private Tools tools = Tools.getTools();
    private Perso pj = PersoManager.getCurrentPJ();

    public RangeRollFactory(Activity mA, Context mC, String mode) {
        this.mA = mA;
        this.mC = mC;
        this.mode = mode;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildRollList();
    }

    private void buildRollList() {
        this.rollList = new RollList();
        String baseAtksTxt = settings.getString("jet_att", mC.getResources().getString(R.string.jet_att_def));     //cherche la clef     jet_att dans les setting sinon valeur def (xml)
        String delim = ",";

        List<Integer> baseAtks = tools.toInt(Arrays.asList(baseAtksTxt.split(delim)));
        List<Integer> allAtks = new ArrayList<>(baseAtks);

        if (this.mode.equalsIgnoreCase("fullround")) {
            Integer prouesse = tools.toInt(settings.getString("prouesse_val", String.valueOf(mC.getResources().getInteger(R.integer.prouesse_def))));
            Integer prouesseAttrib = tools.toInt(settings.getString("prouesse_attrib", String.valueOf(mC.getResources().getInteger(R.integer.prouesse_attrib_def))));
            allAtks.set(prouesseAttrib - 1, prouesse + baseAtks.get(prouesseAttrib - 1));

            if (settings.getBoolean("rapid_enchant_switch", mC.getResources().getBoolean(R.bool.rapid_enchant_switch_def))) {
                allAtks.add(0, allAtks.get(0));
            }
            if (pj.getAllFeats().featIsActive("feat_rapid_fire")) {
                allAtks.add(0, allAtks.get(0));
            }

            if (pj.getAllMythicFeats().mythicFeatsIsActive("mythicfeat_rapid_shot")) {
                allAtks.add(0, allAtks.get(0));
            }

            for (Integer atk : allAtks) {
                Roll roll = new RangedRoll(mA, mC, atk);
                this.rollList.add(roll);
            }
            int nCount = 1;
            for (Roll roll : this.rollList.getList()) {
                roll.setMode(this.mode);
                roll.setNthAtkRoll(nCount);
                nCount++;
            }
        } else {
            Roll roll = new RangedRoll(mA, mC, allAtks.get(0));
            roll.setMode(this.mode);
            this.rollList.add(roll);
        }
    }

    public RollList getRollList() {
        return this.rollList;
    }
}
