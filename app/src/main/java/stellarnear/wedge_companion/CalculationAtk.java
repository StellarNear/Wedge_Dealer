package stellarnear.wedge_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Spells.Metamagic;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;

public class CalculationAtk {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences preferences;

    public CalculationAtk(Context mC){ this.mC=mC;  preferences = PreferenceManager.getDefaultSharedPreferences(mC); }


    public Integer getBonusAtk() {
        int bonusAtk=0;
        if (preferences.getBoolean("isillirit_switch", mC.getResources().getBoolean(R.bool.isillirit_switch_def))) {
            bonusAtk+= 2;
        }
        bonusAtk+= tools.toInt(preferences.getString("attack_att_epic", String.valueOf(mC.getResources().getInteger(R.integer.attack_att_epic_DEF))));

        bonusAtk+= tools.toInt(preferences.getString("bonus_atk_temp", String.valueOf(0)));
        return bonusAtk;
    }

    public Integer getBaseAtk() {
        int baseAtk=0;
        String baseAtksTxt = preferences.getString("jet_att", mC.getResources().getString(R.string.jet_att_def));     //cherche la clef     jet_att dans les setting sinon valeur def (xml)
        String delim = ",";
        List<Integer> baseAtks = new Tools().toInt(Arrays.asList(baseAtksTxt.split(delim)));
        baseAtk=baseAtks.get(0);
        return baseAtk;
    }
}
