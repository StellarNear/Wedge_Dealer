package stellarnear.wedge_dealer.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class RollFactory {

    private Activity mA;
    private Context mC;
    private String mode;
    private RollList rollList;
    private SharedPreferences settings;
    private Tools tools=new Tools();
    public RollFactory(Activity mA,Context mC,String mode){
        this.mA=mA;
        this.mC=mC;
        this.mode=mode;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildRollList();
    }

    private void buildRollList(){
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
            if (settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
                allAtks.add(0, allAtks.get(0));
            }
            for (Integer atk : allAtks) {
                Roll roll = new Roll(mA, mC, atk);
                this.rollList.add(roll);
            }
            int nCount=1;
            for (Roll roll : this.rollList.getList()){
                roll.getAtkRoll().setMode(this.mode);
                roll.setNthRoll(nCount);
                nCount++;
            }
        } else {
            Roll roll = new Roll(mA, mC, allAtks.get(0));
            roll.getAtkRoll().setMode(this.mode);
            this.rollList.add(roll);
        }
    }

    public RollList getRollList() {
        return this.rollList;
    }
}
