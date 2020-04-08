package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public abstract class DmgRoll {
    protected Activity mA;
    protected Context mC;
    protected SharedPreferences settings;
    protected Boolean critConfirmed;
    protected Boolean naturalCrit;
    protected Integer critMultiplier;
    protected boolean manualDiceDmg;
    protected String mode;

    protected int bonusDmg = 0;

    protected DiceList allDiceList = new DiceList();

    protected Tools tools = Tools.getTools();
    protected Perso pj = PersoManager.getCurrentPJ();

    public DmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit) {
        this.mA = mA;
        this.mC = mC;
        this.critConfirmed = critConfirmed;
        this.naturalCrit = naturalCrit;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.manualDiceDmg = settings.getBoolean("switch_manual_diceroll_damage", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public abstract void setDmgRand();

    // Getters

    public DiceList getDmgDiceList() {
        return allDiceList;
    }

    public int getSumDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int sumDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                sumDmg += dice.getRandValue() * critMultiplier;
            } else {
                sumDmg += dice.getRandValue();
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            sumDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            sumDmg += bonusDmg;
        }
        return sumDmg;
    }

    public int getMaxDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int maxDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                maxDmg += dice.getnFace() * critMultiplier;
            } else {
                maxDmg += dice.getnFace();
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            maxDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            maxDmg += bonusDmg;
        }
        return maxDmg;
    }

    public int getMinDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int minDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                minDmg += 1 * critMultiplier;
            } else {
                minDmg += 1;
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            minDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            minDmg += bonusDmg;
        }
        return minDmg;
    }

    public Integer getCritMultiplier() {
        return critMultiplier;
    }


    public abstract int getBonusDmg();
}
