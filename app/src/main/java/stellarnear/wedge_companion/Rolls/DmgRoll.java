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

public class DmgRoll {
    protected Activity mA;
    protected Context mC;
    protected SharedPreferences settings;
    protected Boolean critConfirmed;
    protected Boolean naturalCrit;
    protected Integer critMultiplier;
    protected boolean manualDiceDmg;

    protected int bonusDmg = 0;

    protected DiceList allDiceList = new DiceList();

    protected Tools tools = new Tools();
    protected Perso pj = PersoManager.getCurrentPJ();

    public DmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit) {
        this.mA=mA;
        this.mC = mC;
        this.critConfirmed = critConfirmed;
        this.naturalCrit=naturalCrit;

        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.manualDiceDmg = settings.getBoolean("switch_manual_diceroll_damage", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));

    }

    public void setDmgRand() {
        setCritMultiplier();
        addDmgDices();
        this.bonusDmg = getBonusDmg();
        for (Dice dice : allDiceList.getList()) {
            dice.rand(manualDiceDmg);
        }
    }

    private void setCritMultiplier() {
        if (pj.getAllMythicFeats().mythicFeatsIsActive("mythicfeat_crit_science")) {
            critMultiplier = 4;
        } else {
            critMultiplier = 3;
        }
    }

    private void addDmgDices() {
        Dice dice = new Dice(mA, mC, 8);
        if (critConfirmed) {
            dice.makeCritable();
        }
        allDiceList.add(dice);

    }

    public int getBonusDmg() { //par defaut aucun bonus
        int calcBonusDmg = 0;
        calcBonusDmg += tools.toInt(settings.getString("bonus_dmg_temp", String.valueOf(0)));
        return calcBonusDmg;
    }

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
}
