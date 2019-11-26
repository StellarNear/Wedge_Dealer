package stellarnear.wedge_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;

public class CalculationAtk {

    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences preferences;

    public CalculationAtk(Context mC){ this.mC=mC;  preferences = PreferenceManager.getDefaultSharedPreferences(mC); }


    public Integer getBonusAtk() {
        int bonusAtk=0;

        try {
            int isilDefId = mC.getResources().getIdentifier("isillirit_switch_def"+ PersoManager.getPJExtension(), "bool", mC.getPackageName());
            if (preferences.getBoolean("isillirit_switch"+PersoManager.getPJExtension(), mC.getResources().getBoolean(isilDefId))) {
                bonusAtk+= 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int valDefId = mC.getResources().getIdentifier("attack_att_epic_DEF"+PersoManager.getPJExtension(), "integer", mC.getPackageName());
            int valDef = mC.getResources().getInteger(valDefId);
            bonusAtk+= tools.toInt(preferences.getString("attack_att_epic"+PersoManager.getPJExtension(), String.valueOf(valDef)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bonusAtk+= tools.toInt(preferences.getString("bonus_atk_temp"+PersoManager.getPJExtension(), String.valueOf(0)));
        return bonusAtk;
    }

    public Integer getBaseAtk() {
        int baseAtk=0;
        try {
            int valDefId = mC.getResources().getIdentifier("jet_att_def"+PersoManager.getPJExtension(), "string", mC.getPackageName());
            String valDef = mC.getResources().getString(valDefId);
            String baseAtksTxt = preferences.getString("jet_att"+PersoManager.getPJExtension(), valDef);     //cherche la clef     jet_att dans les setting sinon valeur def (xml)
            String delim = ",";
            List<Integer> baseAtks = new Tools().toInt(Arrays.asList(baseAtksTxt.split(delim)));
            baseAtk=baseAtks.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseAtk;
    }
}
